package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class BucketItemWriter implements ItemWriter<Bucket> {
	private static final String SQL = """
			INSERT INTO buckets (bucket, view_id)
			SELECT :bucket, view_id
			FROM views v
			JOIN collections c USING (collection_id)
			WHERE c.name = :collectionName
			AND v.name = :viewName
			ON CONFLICT (bucket, view_id) DO UPDATE SET bucket_id = buckets.bucket_id
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public BucketItemWriter(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void write(Chunk<? extends Bucket> chunk) {
		for (var bucket : chunk) {
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(
					SQL,
					new MapSqlParameterSource(Map.of(
							"bucket", bucket.getBucketDescriptorAsString(),
							"collectionName", bucket.getViewName().getCollectionName(),
							"viewName", bucket.getViewName().getViewName()
					)),
					keyHolder,
					new String[]{"bucket_id"}
			);
			bucket.setBucketId(Objects.requireNonNull(keyHolder.getKey()).longValue());
		}
	}
}
