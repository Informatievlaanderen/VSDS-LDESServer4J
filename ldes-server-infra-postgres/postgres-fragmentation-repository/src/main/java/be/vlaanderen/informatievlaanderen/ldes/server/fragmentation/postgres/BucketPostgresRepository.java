package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Repository
public class BucketPostgresRepository implements BucketRepository {
	private final ViewEntityRepository viewEntityRepository;
	private final BucketEntityRepository bucketEntityRepository;

	public BucketPostgresRepository(ViewEntityRepository viewEntityRepository, BucketEntityRepository bucketEntityRepository) {
		this.viewEntityRepository = viewEntityRepository;
		this.bucketEntityRepository = bucketEntityRepository;
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
		ViewEntity view = viewEntityRepository.findByViewName(bucket.getViewName().getCollectionName(), bucket.getViewName().getViewName())
				.orElseThrow();

		BucketEntity bucketEntity = new BucketEntity(null, view, bucket.getBucketDescriptorAsString());
		bucketEntity = bucketEntityRepository.save(bucketEntity);

		return new Bucket(
				Objects.requireNonNull(bucketEntity.getBucketId()),
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
