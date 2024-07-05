package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class RootBucketRetriever {
	private final ViewName viewName;
	private final BucketRepository bucketRepository;
	private final ObservationRegistry observationRegistry;
	private Bucket rootBucket;

	public RootBucketRetriever(ViewName viewName, BucketRepository bucketRepository, ObservationRegistry observationRegistry) {
		this.viewName = viewName;
		this.bucketRepository = bucketRepository;
		this.observationRegistry = observationRegistry;
	}

	public Bucket retrieveRootBucket(Observation parentObservation) {
		final Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view %s".formatted(viewName.asString()), observationRegistry)
				.parentObservation(parentObservation)
				.start();

		if(rootBucket == null) {
			rootBucket = bucketRepository.retrieveRootBucket(viewName)
					.orElseThrow(() -> new MissingRootFragmentException(viewName.asString()));
		}

		rootRetrievalObservation.stop();
		return rootBucket;
	}
}
