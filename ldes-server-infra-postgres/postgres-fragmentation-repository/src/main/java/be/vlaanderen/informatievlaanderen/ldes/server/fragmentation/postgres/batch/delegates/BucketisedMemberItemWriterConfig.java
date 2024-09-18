package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BucketisedMemberItemWriterConfig {
	private static final String SQL = """
			 INSERT INTO page_members (bucket_id, member_id)
			 VALUES (?, ?)
			""";

	@Bean
	public ItemWriter<BucketisedMember> bucketisedMemberWriter(DataSource dataSource) {
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
