package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class BucketPostgresRepository implements BucketRepository {
	private final BucketEntityRepository bucketEntityRepository;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public BucketPostgresRepository(BucketEntityRepository bucketEntityRepository, NamedParameterJdbcTemplate jdbcTemplate) {
		this.bucketEntityRepository = bucketEntityRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Optional<Bucket> retrieveBucket(ViewName viewName, BucketDescriptor bucketDescriptor) {
		return bucketEntityRepository
				.findBucketEntityByBucketDescriptor(viewName.asString(), bucketDescriptor.asDecodedString())
				.map(BucketMapper::fromProjection);
	}

	@Override
	@Transactional
	public Bucket insertBucket(Bucket bucket) {
		final String sql = """
				    WITH ins AS (
				        INSERT INTO buckets (bucket, view_id)
				                SELECT :bucket, v.view_id
				                FROM views v
				                INNER JOIN collections c ON c.name = :collectionName
				                WHERE v.name = :viewName
				                ON CONFLICT (bucket, view_id) DO UPDATE SET bucket = EXCLUDED.bucket
				                RETURNING bucket_id
				          )
				          SELECT bucket_id FROM ins;
				""";
		MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
				"bucket", bucket.getBucketDescriptorAsString(),
				"viewName", bucket.getViewName().getViewName(),
				"collectionName", bucket.getViewName().getCollectionName()
		));
		Long bucketId = jdbcTemplate.queryForObject(sql, params, Long.class);
		return new Bucket(
				Objects.requireNonNull(bucketId),
				bucket.getBucketDescriptor(),
				bucket.getViewName()
		);
	}

	@Override
	public Optional<Bucket> retrieveRootBucket(ViewName viewName) {
		return bucketEntityRepository
				.findBucketEntityByBucketDescriptor(viewName.asString(), "")
				.map(BucketMapper::fromProjection);
	}
}
