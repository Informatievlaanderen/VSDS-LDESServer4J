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

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class PageRelationProcessor implements ItemProcessor<Page, List<PageAssignment>> {
	static final String SELECT_UNPROCESSED_MEMBER_COUNT = "SELECT count(*) FROM page_members WHERE bucket_id = ? AND page_id IS NULL";
	static final String INSERT_NEW_PAGE_SQL = "INSERT INTO pages (bucket_id, expiration, partial_url) VALUES (?, NULL, ?)";
	static final String MARK_PAGE_IMMUTABLE_SQL = "UPDATE pages SET immutable = true WHERE page_id = ?";
	static final String INSERT_PAGE_RELATION_SQL = "INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, '" + RdfConstants.GENERIC_TREE_RELATION + "')";

	private final JdbcTemplate jdbcTemplate;

	public PageRelationProcessor(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<PageAssignment> process(@NotNull Page page) {
		// Get Items count to process
		int unprocessedMemberCount = Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_UNPROCESSED_MEMBER_COUNT, Integer.class, page.getBucketId()));

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
		final PartialUrl childPagePartialUrl = page.createChildPartialUrl();
		final long childPageId = addPageToDb(page.getBucketId(), childPagePartialUrl.asString());
		closeParentIfNecessary(page);
		addRelation(page.getId(), childPageId);
		return new Page(childPageId, page.getBucketId(), childPagePartialUrl, page.getPageSize());
	}

	private long addPageToDb(long bucketId, String childPagePartialUrl) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(INSERT_NEW_PAGE_SQL, new String[]{"page_id"});
			ps.setLong(1, bucketId);
			ps.setString(2, childPagePartialUrl);
			return ps;
		}, keyHolder);
		return Objects.requireNonNull(keyHolder.getKeyAs(Long.class));
	}

	private void closeParentIfNecessary(Page parentPage) {
		if (!parentPage.isNumberLess()) {
			jdbcTemplate.update(MARK_PAGE_IMMUTABLE_SQL, parentPage.getId());
		}
	}

	private void addRelation(long parentPageId, long childPageId) {
		jdbcTemplate.update(INSERT_PAGE_RELATION_SQL, parentPageId, childPageId);

	}
}
