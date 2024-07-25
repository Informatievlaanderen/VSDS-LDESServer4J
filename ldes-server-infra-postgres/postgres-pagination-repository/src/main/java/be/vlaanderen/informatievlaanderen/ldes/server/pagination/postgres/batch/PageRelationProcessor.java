package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PartialUrl;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;

@Component
public class PageRelationProcessor implements ItemProcessor<Page, List<PageAssignment>> {
	public static final String SELECT_UNPROCESSED = "SELECT count(*) FROM page_members WHERE bucket_id = ? AND page_id IS NULL";
	public static final String INSERT_NEW_PAGE_SQL = "INSERT INTO pages (bucket_id, expiration, partial_url) VALUES (?, NULL, ?)";
	public static final String MARK_PAGE_IMMUTABLE_SQL = "UPDATE pages SET immutable = true WHERE page_id = ?";
	public static final String INSERT_PAGE_RELATION_SQL = "INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, ?)";

	private final JdbcTemplate jdbcTemplate;

	public PageRelationProcessor(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<PageAssignment> process(@NotNull Page page) {
		// Get Items count to process
		int unprocessedMemberCount = Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_UNPROCESSED, Integer.class, page.getBucketId()));

		Page pageToFill = page;
		List<PageAssignment> pageAssignments = new ArrayList<>();

		while (0 < unprocessedMemberCount) {
			pageToFill = getPageWithSpace(pageToFill);
			int membersAdded = fillPage(pageToFill, unprocessedMemberCount);
			unprocessedMemberCount -= membersAdded;
			pageAssignments.add(new PageAssignment(pageToFill.getId(), pageToFill.getBucketId(), membersAdded));
		}

		return pageAssignments;
	}

	private int fillPage(Page pageToFile, int numOfMembersToAdd) {
		int membersAdded = Math.min(numOfMembersToAdd, pageToFile.getAvailableMemberSpace());
		pageToFile.incrementAssignedMemberCount(membersAdded);
		return membersAdded;
	}

	private Page getPageWithSpace(Page page) {
		if (page.isNumberLess() || page.isFull()) {
			return createNewPage(page);
		}
		return page;
	}


	private Page createNewPage(Page page) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final PartialUrl childPagePartialUrl = page.createChildPartialUrl();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(INSERT_NEW_PAGE_SQL, new String[]{"page_id"});
			ps.setLong(1, page.getBucketId());
			ps.setString(2, childPagePartialUrl.asString());
			return ps;
		}, keyHolder);
		if (!page.isNumberLess()) {
			jdbcTemplate.update(MARK_PAGE_IMMUTABLE_SQL, page.getId());
		}
		jdbcTemplate.update(INSERT_PAGE_RELATION_SQL, page.getId(), keyHolder.getKey(), RdfConstants.GENERIC_TREE_RELATION);
		return new Page(
				Objects.requireNonNull(keyHolder.getKeyAs(Long.class)),
				page.getBucketId(),
				childPagePartialUrl,
				page.getMaximumMemberCount()
		);
	}
}
