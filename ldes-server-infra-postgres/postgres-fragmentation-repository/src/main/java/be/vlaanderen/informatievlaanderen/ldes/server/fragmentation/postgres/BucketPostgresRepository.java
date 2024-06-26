package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class BucketPostgresRepository implements BucketRepository {
	private final BucketEntityRepository bucketEntityRepository;

	public BucketPostgresRepository(BucketEntityRepository bucketEntityRepository) {
		this.bucketEntityRepository = bucketEntityRepository;
	}

	@Override
	public Optional<Bucket> retrieveBucket(String bucketDescriptor) {
		return bucketEntityRepository
				.findBucketEntityByBucketDescriptor(bucketDescriptor)
				.map(BucketMapper::fromProjection);
	}

	@Override
	@Transactional
	public void insertBucket(Bucket bucket) {
		bucketEntityRepository.insertBucketEntity(bucket.getBucketDescriptorAsString(), bucket.getViewName().asString());
	}
}
