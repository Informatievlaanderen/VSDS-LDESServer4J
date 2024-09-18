package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.Objects;

import static io.micrometer.observation.Observation.createNotStarted;

public class FragmentationStrategyBatchExecutor {

	private final FragmentationStrategy fragmentationStrategy;
	private final ViewName viewName;
	private final ObservationRegistry observationRegistry;

	@SuppressWarnings("java:S107")
	public FragmentationStrategyBatchExecutor(ViewName viewName,
	                                          FragmentationStrategy fragmentationStrategy,
	                                          ObservationRegistry observationRegistry) {
		this.observationRegistry = observationRegistry;
		this.fragmentationStrategy = fragmentationStrategy;
		this.viewName = viewName;
    }

	public Bucket bucketise(FragmentationMember member) {
		final Observation parentObservation = createNotStarted("execute fragmentation", observationRegistry).start();
		final Bucket rootBucket = Bucket.createRootBucketForView(viewName);
		fragmentationStrategy.addMemberToBucket(rootBucket, member, parentObservation);
		parentObservation.stop();
		return rootBucket;
	}

	public boolean isPartOfCollection(String collectionName) {
		return Objects.equals(viewName.getCollectionName(), collectionName);
	}

	public ViewName getViewName() {
		return viewName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FragmentationStrategyBatchExecutor that = (FragmentationStrategyBatchExecutor) o;
		return Objects.equals(getViewName(), that.getViewName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getViewName());
	}

}
