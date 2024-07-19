package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class PageRelationProcessor implements ItemProcessor<Page, List<Page>> {
	public static final String SELECT_UNPROCESSED = "SELECT count(*) FROM page_members WHERE bucket_id = ? AND page_id IS NULL";
	public static final String INSERT_NEW_PAGE_SQL = "INSERT INTO pages (bucket_id, expiration, partial_url) VALUES (?, NULL, ?)";
	public static final String MARK_PAGE_IMMUTABLE_SQL = "UPDATE pages SET immutable = true WHERE page_id = ?";
	public static final String INSERT_PAGE_RELATION_SQL = "INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, ?)";

	private final JdbcTemplate jdbcTemplate;

	public PageRelationProcessor(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Page> process(@NotNull Page page) {
		// Get Items count to process
		int unprocessedMemberCount = jdbcTemplate.queryForObject(SELECT_UNPROCESSED, Integer.class, page.getBucketId());

		List<Page> assignedPages = new ArrayList<>();

		while (0 < unprocessedMemberCount) {
			Page assignedPage = assignToPage(page, unprocessedMemberCount);
			assignedPages.add(assignedPage);
			unprocessedMemberCount =- assignedPage.getAssignedMemberCount();
		}

		return assignedPages;
	}

	private Page assignToPage(Page page, int memberCount) {
		if (page.isNumberLess() || page.isFull()) {
			page = createNewPage(page);
		}

		int assignedMemberCount = Math.min(memberCount, page.getMaximumMemberCount() - page.getAssignedMemberCount());
		page.setAssignedMemberCount(assignedMemberCount);
		return page;
	}

	private Page createNewPage(Page page) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final String childPagePartialUrl = page.createChildPartialUrl();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(INSERT_NEW_PAGE_SQL, new String[] {"page_id"});
			ps.setLong(1, page.getBucketId());
			ps.setString(2, childPagePartialUrl);
			return ps;
		}, keyHolder);
		if(!page.isNumberLess()) {
			jdbcTemplate.update(MARK_PAGE_IMMUTABLE_SQL, page.getId());
		}
		jdbcTemplate.update(INSERT_PAGE_RELATION_SQL, page.getId(), keyHolder.getKey(), RdfConstants.GENERIC_TREE_RELATION);
		return Page.createWithPartialUrl(
				Objects.requireNonNull(keyHolder.getKeyAs(Long.class)),
				page.getBucketId(),
				childPagePartialUrl,
				0,
				page.getMaximumMemberCount()
		);
	}
}
