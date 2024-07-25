package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class PageAssigner implements ItemWriter<List<PageAssignment>> {
	private static final String SQL = """
			UPDATE page_members
			SET page_id = ?
			WHERE (bucket_id, member_id) IN (SELECT bucket_id, member_id FROM page_members WHERE bucket_id = ? AND page_id IS NULL LIMIT ?)
			""";
	private final JdbcTemplate jdbcTemplate;

	public PageAssigner(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void write(Chunk<? extends List<PageAssignment>> chunk) {
		for (var pages : chunk) {
			final List<Object[]> batchArgs = pages.stream()
					.map(assignment -> new Object[]{assignment.pageId(), assignment.bucketId(), assignment.newlyAssignedMemberCount()})
					.toList();
			jdbcTemplate.batchUpdate(SQL, batchArgs);
		}
	}
}
