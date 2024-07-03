package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PaginationPage;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class PageReader {
	private static final String SELECT_PAGE_SQL = """
			SELECT DISTINCT p.page_id, p.bucket_id, p.partial_url, v.page_size - COUNT(member_id) AS available_member_capacity
			FROM pages p
			         LEFT JOIN page_members m ON p.page_id = m.page_id
			         JOIN buckets b ON p.bucket_id = b.bucket_id
			         JOIN views v ON v.view_id = b.view_id
			WHERE b.bucket_id = ?
			  AND p.page_id NOT IN (SELECT from_page_id FROM page_relations)
			GROUP BY p.page_id, v.page_size;
			""";

	@Bean
	@StepScope
	public ItemReader<PaginationPage> pageItemReader(DataSource dataSource,
	                                                 @Value("#{stepExecutionContext}") Map<String, Object> stepExecutionContext) {
		final ExecutionContext executionContext = new ExecutionContext(stepExecutionContext);
		final var reader = new JdbcCursorItemReaderBuilder<PaginationPage>()
				.dataSource(dataSource)
				.sql(SELECT_PAGE_SQL)
				.queryArguments(executionContext.getLong("bucketId"))
				.rowMapper(new PaginationRowMapper())
				.saveState(false)
				.build();
		reader.open(executionContext);
		return reader;
	}

}