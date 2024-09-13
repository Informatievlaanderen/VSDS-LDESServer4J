package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {
	private final FragmentationStrategy fragmentationStrategy;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy) {
		this.fragmentationStrategy = fragmentationStrategy;
	}

	@Override
	public void addMemberToBucket(Bucket parentBucket, FragmentationMember member, Observation parentObservation) {
		fragmentationStrategy.addMemberToBucket(parentBucket, member, parentObservation);
	}

}
