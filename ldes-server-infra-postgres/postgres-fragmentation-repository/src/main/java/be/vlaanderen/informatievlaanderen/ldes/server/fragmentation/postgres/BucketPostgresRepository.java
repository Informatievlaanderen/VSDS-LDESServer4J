package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BucketPostgresRepository implements BucketRepository {
	private final BucketEntityRepository bucketEntityRepository;
	private final ViewEntityRepository viewEntityRepository;

	public BucketPostgresRepository(BucketEntityRepository bucketEntityRepository, ViewEntityRepository viewEntityRepository) {
		this.bucketEntityRepository = bucketEntityRepository;
		this.viewEntityRepository = viewEntityRepository;
	}

	@Override
	public Optional<Bucket> retrieveBucket(String bucketDescriptor) {
		return bucketEntityRepository
				.findBucketEntityByBucketDescriptor(bucketDescriptor)
				.map(BucketMapper::fromProjection);
	}

	@Override
	public void saveBucket(Bucket bucket) {
		viewEntityRepository.findByViewName(bucket.getViewName().getCollectionName(), bucket.getViewName().getViewName())
				.map(view -> new BucketEntity(view, bucket.getBucketDescriptorAsString()))
				.ifPresent(bucketEntityRepository::save);
	}
}
