package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.UnpagedMember;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationJobDefinitions.CHUNK_SIZE;


@Component
@StepScope
public class UnpagedReader implements ItemStreamReader<List<UnpagedMember>> {
	private final ItemStreamReader<UnpagedMember> delegate;

	public UnpagedReader(ItemStreamReader<UnpagedMember> delegate) {
		this.delegate = delegate;
	}

	@Override
	public List<UnpagedMember> read() throws Exception {
		List<UnpagedMember> items = new ArrayList<>(CHUNK_SIZE);

		for (int i = 0; i < CHUNK_SIZE; i++) {
			UnpagedMember item = delegate.read();
			if (item == null) {
				// If there are no more items, return what we have (even if it's less than the bundle size)
				return items.isEmpty() ? null : items;
			}
			items.add(item);
		}

		return items;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		delegate.close();
	}

	@Bean
	@StepScope
	JdbcCursorItemReader<UnpagedMember> delegateReader(DataSource dataSource,
	                                                     @Value("#{stepExecutionContext['bucketId']}") Long bucketId) {
		return new JdbcCursorItemReaderBuilder<UnpagedMember>()
				.name("unpagedReader")
				.dataSource(dataSource)
				.sql("""
						SELECT member_id, bucket_id
						FROM page_members
						WHERE bucket_id = ?
						AND page_id IS NULL
						ORDER BY member_id
						""")
				.preparedStatementSetter(ps -> ps.setLong(1, bucketId))
				.rowMapper((rs, rowNum) -> new UnpagedMember(rs.getLong(1), rs.getLong(2)))
				.build();
	}
}