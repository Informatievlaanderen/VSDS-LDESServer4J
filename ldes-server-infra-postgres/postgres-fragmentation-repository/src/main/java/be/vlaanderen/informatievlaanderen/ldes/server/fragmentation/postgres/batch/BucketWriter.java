package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Component
public class BucketWriter implements ItemWriter<Bucket> {
	private final ItemWriter<Bucket> bucketDelegate;
	private final ItemWriter<BucketRelation> bucketRelationDelegate;

	public BucketWriter(@Qualifier("batchBucketWriter") ItemWriter<Bucket> bucketDelegate,
	                    ItemWriter<BucketRelation> bucketRelationDelegate) {
		this.bucketDelegate = bucketDelegate;
		this.bucketRelationDelegate = bucketRelationDelegate;
	}

	@Override
	public void write(Chunk<? extends Bucket> chunk) throws Exception {
		final Chunk<Bucket> bucketTreeChunk = new Chunk<>(chunk.getItems().stream()
				.flatMap(bucket -> bucket.getBucketTree().stream())
				.distinct()
				.toList());
		bucketDelegate.write(bucketTreeChunk);
		// TODO: (or should the trigger stay in place?)
		/*
		final String pageSql = "INSERT INTO pages (bucket_id, expiration, partial_url) VALUES (:bucketId, NULL, :partialUrl)";
		final MapSqlParameterSource[] pageBatchArgs = bucketTreeChunk.stream()
				.map(bucket -> new MapSqlParameterSource(Map.of(
						"bucketId", bucket.getBucketId(),
						"partialUrl", bucket.createPartialUrl()
				)))
				.toArray(MapSqlParameterSource[]::new);
		jdbcTemplate.batchUpdate(pageSql, pageBatchArgs);
		 */
		final Chunk<BucketRelation> bucketRelationChunk = new Chunk<>(chunk.getItems().stream()
				.flatMap(bucket -> bucket.getAllRelations().stream())
				.toList()
		);
		bucketRelationDelegate.write(bucketRelationChunk);
	}

	@Configuration
	public static class BatchBucketWriterConfig {
		private static final String SQL = """
				INSERT INTO buckets (bucket, view_id)
				SELECT :bucket, view_id
				FROM views v
				JOIN collections c USING (collection_id)
				WHERE c.name = :collectionName
				AND :v.name = :viewName
				ON CONFLICT (bucket, view_id) DO NOTHING
				""";

		@Bean
		ItemWriter<Bucket> batchBucketWriter(DataSource dataSource) {
			return new JdbcBatchItemWriterBuilder<Bucket>()
					.dataSource(dataSource)
					.sql(SQL)
					.itemSqlParameterSourceProvider(item -> new MapSqlParameterSource(Map.of(
							"bucket", item.getBucketDescriptorAsString(),
							"collectionName", item.getViewName().getCollectionName(),
							"viewName", item.getViewName().getViewName()
					)))
					.build();

		}

		@Bean
		ItemWriter<BucketRelation> bucketRelationItemWriter(DataSource dataSource) {
			final String sql = """
					INSERT INTO page_relations (from_page_id, to_page_id, relation_type, value, value_type, path)
					SELECT (SELECT page_id FROM pages WHERE partial_url = :fromPagePartialUrl),
					       (SELECT page_id FROM pages WHERE partial_url = :toPagePartialUrl),
					       :treeRelationType, :treeValue, :treeValueType, :treePath
					""";
			return new JdbcBatchItemWriterBuilder<BucketRelation>()
					.dataSource(dataSource)
					.sql(sql)
					.itemSqlParameterSourceProvider(item -> new MapSqlParameterSource(Map.of(
							"fromPagePartialUrl", item.fromPagePartialUrl(),
							"toPagePartialUrl", item.toPagePartialUrl(),
							"treeRelationType", item.relation().treeRelationType(),
							"treeValue", item.relation().treeValue(),
							"treeValueType", item.relation().treeValueType(),
							"treePath", item.relation().treePath()
					)))
					.build();
		}
	}
}
