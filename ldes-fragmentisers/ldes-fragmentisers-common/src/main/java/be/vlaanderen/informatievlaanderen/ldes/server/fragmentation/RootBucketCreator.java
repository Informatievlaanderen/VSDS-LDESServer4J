package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class RootBucketCreator {

	private final BucketRepository bucketRepository;
	private final ObservationRegistry observationRegistry;
	private Bucket rootBucket;

	public RootBucketCreator(BucketRepository bucketRepository, ObservationRegistry observationRegistry) {
		this.bucketRepository = bucketRepository;
		this.observationRegistry = observationRegistry;
	}

	public Bucket getOrCreateRootBucket(ViewName viewName, Observation parentObservation) {
		Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();

		if(rootBucket == null) {
			rootBucket = bucketRepository
					.retrieveRootBucket(viewName)
					.orElseGet(() -> {
						final Bucket bucket = Bucket.createRootBucketForView(viewName);
						bucketRepository.insertBucket(bucket);
						return bucket;
					});
		}

		rootRetrievalObservation.stop();
		return rootBucket;
	}

}
