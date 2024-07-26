package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class PageAssigner implements ItemWriter<Page> {
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
	public void write(Chunk<? extends Page> chunk) throws Exception {
		for (var page : chunk) {
			jdbcTemplate.update(SQL, page.getId(), page.getBucketId(), page.getAvailableMemberSpace());
		}
	}
}
