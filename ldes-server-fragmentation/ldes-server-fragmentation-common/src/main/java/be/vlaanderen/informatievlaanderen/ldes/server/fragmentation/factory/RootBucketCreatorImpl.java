package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import org.springframework.stereotype.Component;

@Component
public class RootBucketCreatorImpl implements RootBucketCreator {
	private final BucketRepository bucketRepository;

	public RootBucketCreatorImpl(BucketRepository bucketRepository) {
		this.bucketRepository = bucketRepository;
	}

	@Override
	public void createRootBucketForView(ViewName viewName) {
		if (bucketRepository.retrieveRootBucket(viewName).isEmpty()) {
			bucketRepository.insertRootBucket(Bucket.createRootBucketForView(viewName));
		}
	}
}
