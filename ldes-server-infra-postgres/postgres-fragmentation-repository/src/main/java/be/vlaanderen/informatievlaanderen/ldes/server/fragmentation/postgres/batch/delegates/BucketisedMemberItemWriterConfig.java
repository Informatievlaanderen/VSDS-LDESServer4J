package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class BucketisedMemberItemWriterConfig {
	private static final String SQL = """
			 INSERT INTO page_members (bucket_id, member_id, view_id)
			 SELECT :bucketId, :memberId, view_id
			 FROM buckets WHERE bucket_id = :bucketId
			""";

	@Bean
	public ItemWriter<BucketisedMember> bucketisedMemberWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<BucketisedMember>()
				.dataSource(dataSource)
				.sql(SQL)
				.itemSqlParameterSourceProvider(item -> new MapSqlParameterSource(Map.of(
						"bucketId", item.bucketId(),
						"memberId", item.memberId()
				)))
				.build();
	}
}
