package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class BucketPostgresRepository implements BucketRepository {
	private final ViewEntityRepository viewEntityRepository;
	private final BucketEntityRepository bucketEntityRepository;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public BucketPostgresRepository(ViewEntityRepository viewEntityRepository, BucketEntityRepository bucketEntityRepository, NamedParameterJdbcTemplate jdbcTemplate) {
		this.viewEntityRepository = viewEntityRepository;
		this.bucketEntityRepository = bucketEntityRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public Bucket insertRootBucket(Bucket bucket) {
		String sql = """
				INSERT INTO pages (bucket_id, expiration, partial_url, is_root)
				VALUES (:bucketId, NULL, :partialUrl, true)
				ON CONFLICT DO NOTHING
				""";
		ViewEntity view = viewEntityRepository.findByViewName(bucket.getViewName().getCollectionName(), bucket.getViewName().getViewName())
				.orElseThrow();

		BucketEntity bucketEntity = new BucketEntity(view, bucket.getBucketDescriptorAsString());
		bucketEntity = bucketEntityRepository.save(bucketEntity);
		long bucketId = Objects.requireNonNull(bucketEntity.getBucketId());
		jdbcTemplate.update(sql, Map.of("bucketId", bucketId, "partialUrl", bucket.createPartialUrl()));

		return new Bucket(
				bucketId,
				bucket.getBucketDescriptor(),
				bucket.getViewName(),
				List.of(),
				0
		);
	}

	@Override
	public Optional<Bucket> retrieveRootBucket(ViewName viewName) {
		return bucketEntityRepository
				.findBucketEntityByBucketDescriptor(viewName.asString(), "")
				.map(BucketMapper::fromProjection);
	}
}
