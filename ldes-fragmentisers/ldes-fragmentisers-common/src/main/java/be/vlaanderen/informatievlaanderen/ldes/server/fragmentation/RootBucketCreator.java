package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class RootBucketCreator {

	private final BucketRepository bucketRepository;
	private final FragmentRepository fragmentRepository;
	private final ObservationRegistry observationRegistry;
	private Fragment rootFragment;

	public RootBucketCreator(BucketRepository bucketRepository, FragmentRepository fragmentRepository, ObservationRegistry observationRegistry) {
		this.bucketRepository = bucketRepository;
		this.fragmentRepository = fragmentRepository;
		this.observationRegistry = observationRegistry;
	}

	public Fragment retrieveRootFragmentOfView(ViewName viewName, Observation parentObservation) {
		Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();

		if (rootFragment == null) {
			rootFragment = fragmentRepository
					.retrieveRootFragment(viewName.asString())
					.orElseThrow(() -> new MissingRootFragmentException(viewName.asString()));

		}

		rootRetrievalObservation.stop();
		return rootFragment;
	}

	public Bucket getOrCreateRootBucket(ViewName viewName, Observation parentObservation) {
		Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();

		final Bucket rootBucket = bucketRepository
				.retrieveRootBucket(viewName)
				.orElseGet(() -> {
					final Bucket bucket = Bucket.createRootBucketForView(viewName);
					bucketRepository.insertBucket(bucket);
					return bucket;
				});

		rootRetrievalObservation.stop();
		return rootBucket;
	}

}
