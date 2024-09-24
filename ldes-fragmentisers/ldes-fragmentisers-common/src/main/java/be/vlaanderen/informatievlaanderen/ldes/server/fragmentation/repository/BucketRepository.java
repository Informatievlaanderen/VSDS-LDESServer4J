package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;

import java.util.Optional;

public interface BucketRepository {
	Bucket insertBucket(Bucket bucket);
	Optional<Bucket> retrieveRootBucket(ViewName viewName);
}
