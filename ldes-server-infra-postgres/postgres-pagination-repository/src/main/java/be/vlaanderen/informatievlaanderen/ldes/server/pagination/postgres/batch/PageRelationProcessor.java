package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PaginationPage;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Objects;

@Component
public class PageRelationProcessor implements ItemProcessor<PaginationPage, PaginationPage> {
	public static final String SELECT_PAGE_SIZE_SQL = "SELECT v.page_size FROM views v JOIN buckets b USING (view_id) WHERE bucket_id = ?";
	public static final String INSERT_NEW_PAGE_SQL = "INSERT INTO pages (bucket_id, expiration, partial_url) VALUES (?, NULL, ?)";
	public static final String MARK_PAGE_IMMUTABLE_SQL = "UPDATE pages SET immutable = true WHERE page_id = ?";
	public static final String INSERT_PAGE_RELATION_SQL = "INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, ?)";

	private final JdbcTemplate jdbcTemplate;

	public PageRelationProcessor(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public PaginationPage process(@NotNull PaginationPage page) throws Exception {
		if (page.isNumberLess() || page.isFull()) {
			final Integer capacity = jdbcTemplate.queryForObject(SELECT_PAGE_SIZE_SQL, Integer.class, page.getBucketId());
			final KeyHolder keyHolder = new GeneratedKeyHolder();
			final String childPagePartialUrl = page.createChildPartialUrl();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(INSERT_NEW_PAGE_SQL, new String[] {"page_id"});
				ps.setLong(1, page.getBucketId());
				ps.setString(2, childPagePartialUrl);
				return ps;
			}, keyHolder);
			jdbcTemplate.update(MARK_PAGE_IMMUTABLE_SQL, page.getId());
			jdbcTemplate.update(INSERT_PAGE_RELATION_SQL, page.getId(), keyHolder.getKey(), RdfConstants.GENERIC_TREE_RELATION);
			return PaginationPage.createWithPartialUrl(
					Objects.requireNonNull(keyHolder.getKeyAs(Long.class)),
					page.getBucketId(),
					childPagePartialUrl,
					Objects.requireNonNull(capacity)
			);
		}
		return page;
	}
}
