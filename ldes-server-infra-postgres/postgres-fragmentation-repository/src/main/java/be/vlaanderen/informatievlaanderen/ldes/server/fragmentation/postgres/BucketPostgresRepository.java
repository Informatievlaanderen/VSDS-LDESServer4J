package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql = """
				WITH view_names AS (SELECT v.view_id, concat(c.name, '/' , v.name) AS view_name FROM views v JOIN collections c ON v.collection_id = c.collection_id)
				INSERT INTO buckets (bucket, view_id) SELECT :bucket, v.view_id FROM view_names v WHERE v.view_name = :viewName
				""";
		MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
				"bucket", bucket.getBucketDescriptorAsString(),
				"viewName", bucket.getViewName().asString()
		));
		jdbcTemplate.update(sql, params, keyHolder, new String[]{"bucket_id"});
		return new Bucket(
				Objects.requireNonNull(keyHolder.getKeyAs(Long.class)),
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
