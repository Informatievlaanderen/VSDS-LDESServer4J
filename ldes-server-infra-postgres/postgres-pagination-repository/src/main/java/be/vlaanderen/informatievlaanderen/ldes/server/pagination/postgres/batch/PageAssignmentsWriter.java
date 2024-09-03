package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

@Component
public class PageAssignmentsWriter implements ItemWriter<List<PageAssignment>> {
	private final JdbcBatchItemWriter<PageAssignment> delegateWriter;

	public PageAssignmentsWriter(JdbcBatchItemWriter<PageAssignment> batchItemWriter) {
		this.delegateWriter = batchItemWriter;
	}

	@Override

	public void write(Chunk<? extends List<PageAssignment>> chunk) throws Exception {
		var items = chunk.getItems()
				.stream()
				.flatMap(Collection::stream)
				.toList();

		if (!items.isEmpty()) {
			delegateWriter.write(new Chunk<>(items));
		}
	}

	@Configuration
	public static class BatchPageAssignmentWriterConfig {
		private static final String SQL = """
				UPDATE page_members
				SET page_id = ?
				WHERE bucket_id = ? AND member_id = ?
				""";

		@Bean
		JdbcBatchItemWriter<PageAssignment> batchPageAssignmentWriter(DataSource dataSource) {
			return new JdbcBatchItemWriterBuilder<PageAssignment>()
					.dataSource(dataSource)
					.sql(SQL)
					.itemPreparedStatementSetter((item, ps) -> {
						ps.setLong(1, item.pageId());
						ps.setLong(2, item.bucketId());
						ps.setLong(3, item.memberId());
					})
					.build();
		}
	}

}
