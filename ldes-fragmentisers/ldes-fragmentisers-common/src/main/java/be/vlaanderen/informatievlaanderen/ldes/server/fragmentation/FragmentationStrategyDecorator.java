package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelationCreatedEvent;
import io.micrometer.observation.Observation;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {

	private final FragmentationStrategy fragmentationStrategy;

	private final ApplicationEventPublisher applicationEventPublisher;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy,
	                                         ApplicationEventPublisher applicationEventPublisher) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public List<BucketisedMember> addMemberToBucket(Bucket rootFragmentOfView, FragmentationMember member, Observation parentObservation) {
		return fragmentationStrategy.addMemberToBucket(rootFragmentOfView, member, parentObservation);
	}

	protected void addRelationFromParentToChild(Bucket parentBucket, Bucket childBucket) {
		BucketRelation bucketRelation = BucketRelation.createGenericRelation(parentBucket, childBucket);
		applicationEventPublisher.publishEvent(new BucketRelationCreatedEvent(bucketRelation));
	}

}
