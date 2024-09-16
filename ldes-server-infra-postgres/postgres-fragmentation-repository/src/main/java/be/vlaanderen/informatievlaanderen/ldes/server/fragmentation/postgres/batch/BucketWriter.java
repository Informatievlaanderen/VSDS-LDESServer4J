package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BucketWriter implements ItemWriter<Bucket> {
	private final BucketRepository bucketRepository;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public BucketWriter(BucketRepository bucketRepository, NamedParameterJdbcTemplate jdbcTemplate) {
		this.bucketRepository = bucketRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void write(Chunk<? extends Bucket> chunk) {
		final List<Bucket> savedBuckets = chunk.getItems().stream()
				.flatMap(bucket -> bucket.getBucketTree().stream())
				.map(bucketRepository::insertBucket)
				.toList();
		final String pageSql = "INSERT INTO pages (bucket_id, expiration, partial_url) VALUES (:bucketId, NULL, :partialUrl)";
		final MapSqlParameterSource[] pageBatchArgs = savedBuckets.stream()
				.map(bucket -> new MapSqlParameterSource(Map.of(
						"bucketId", bucket.getBucketId(),
						"partialUrl", bucket.createPartialUrl()
				)))
				.toArray(MapSqlParameterSource[]::new);
		jdbcTemplate.batchUpdate(pageSql, pageBatchArgs);
		final String pageRelationsSql = """
				INSERT INTO page_relations (from_page_id, to_page_id, relation_type, value, value_type, path)
				SELECT (SELECT page_id FROM pages WHERE partial_url = :fromPagePartialUrl),
				       (SELECT page_id FROM pages WHERE partial_url = :toPagePartialUrl),
				       :treeRelationType, :treeValue, :treeValueType, :treePath
				""";
		final MapSqlParameterSource[] relationsBatchArgs = chunk.getItems().stream()
				.flatMap(bucket -> bucket.getAllRelations().stream())
				.map(relation -> new MapSqlParameterSource(Map.of(
						"fromPagePartialUrl", relation.fromPagePartialUrl(),
						"toPagePartialUrl", relation.toPagePartialUrl(),
						"treeRelationType", relation.relation().treeRelationType(),
						"treeValue", relation.relation().treeValue(),
						"treeValueType", relation.relation().treeValueType(),
						"treePath", relation.relation().treePath()
				)))
				.toArray(MapSqlParameterSource[]::new);

		jdbcTemplate.batchUpdate(pageRelationsSql, relationsBatchArgs);
	}
}
