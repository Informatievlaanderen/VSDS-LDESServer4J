package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class BucketPageItemWriterConfig {
	private static final String SQL = """
			INSERT INTO pages (bucket_id, expiration, partial_url)
			VALUES (:bucketId, NULL, :partialUrl)
			ON CONFLICT DO NOTHING
			""";

	@Bean
	ItemWriter<Bucket> pageItemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Bucket>()
				.dataSource(dataSource)
				.sql(SQL)
				.assertUpdates(false)
				.itemSqlParameterSourceProvider(item -> new MapSqlParameterSource(Map.of(
						"bucketId", item.getBucketId(),
						"partialUrl", item.createPartialUrl()
				)))
				.build();

	}
}
