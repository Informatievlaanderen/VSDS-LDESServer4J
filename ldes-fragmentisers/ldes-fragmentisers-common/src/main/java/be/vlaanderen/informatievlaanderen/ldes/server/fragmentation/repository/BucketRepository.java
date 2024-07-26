package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;

import java.util.Optional;

public interface BucketRepository {
	Optional<Bucket> retrieveBucket(ViewName viewName, BucketDescriptor bucketDescriptor);

	Bucket insertBucket(Bucket bucket);

	Optional<Bucket> retrieveRootBucket(ViewName viewName);
}
