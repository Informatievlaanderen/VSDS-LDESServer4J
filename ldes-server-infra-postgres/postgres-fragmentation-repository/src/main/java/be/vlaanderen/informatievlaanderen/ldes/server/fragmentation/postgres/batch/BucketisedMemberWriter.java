package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class BucketisedMemberWriter implements ItemWriter<Bucket> {
	private final JdbcBatchItemWriter<BucketisedMember> delegateWriter;

	public BucketisedMemberWriter(JdbcBatchItemWriter<BucketisedMember> delegateWriter) {
		this.delegateWriter = delegateWriter;
	}

	@Override
	public void write(Chunk<? extends Bucket> chunk) throws Exception {
		Chunk<BucketisedMember> bucketisedMembers = new Chunk<>(chunk.getItems()
				.stream()
				.flatMap(bucket -> bucket.getAllMembers().stream())
				.toList());

		if (!bucketisedMembers.isEmpty()) {
			delegateWriter.write(bucketisedMembers);
		}
	}

	@Configuration
	public static class BatchBucketWriterConfig {
		private static final String SQL = """
				 INSERT INTO page_members (bucket_id, member_id)
				 VALUES (?, ?)
				""";

		@Bean
		JdbcBatchItemWriter<BucketisedMember> batchBucketWriter(DataSource dataSource) {
			return new JdbcBatchItemWriterBuilder<BucketisedMember>()
					.dataSource(dataSource)
					.sql(SQL)
					.itemPreparedStatementSetter((item, ps) -> {
						ps.setLong(1, item.bucketId());
						ps.setLong(2, item.memberId());
					})
					.build();
		}
	}
}
