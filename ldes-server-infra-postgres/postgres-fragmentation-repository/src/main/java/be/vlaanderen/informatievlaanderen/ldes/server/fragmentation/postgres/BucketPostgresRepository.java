package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
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
	public Optional<Bucket> retrieveBucket(ViewName viewName, BucketDescriptor bucketDescriptor) {
		return bucketEntityRepository
				.findBucketEntityByBucketDescriptor(viewName.asString(), bucketDescriptor.asDecodedString())
				.map(BucketMapper::fromProjection);
	}

	@Override
	@Transactional
	public void insertBucket(Bucket bucket) {
		bucketEntityRepository.insertBucketEntity(bucket.getBucketDescriptorAsString(), bucket.getViewName().asString());
	}

	@Override
	public Optional<Bucket> retrieveRootBucket(ViewName viewName) {
		return bucketEntityRepository
				.findBucketEntityByBucketDescriptor(viewName.asString(), "")
				.map(BucketMapper::fromProjection);
	}
}
