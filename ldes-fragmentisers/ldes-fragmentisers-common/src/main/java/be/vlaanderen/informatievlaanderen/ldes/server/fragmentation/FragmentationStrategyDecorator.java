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

	private final FragmentRepository fragmentRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy,
	                                         FragmentRepository fragmentRepository,
	                                         ApplicationEventPublisher applicationEventPublisher) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.fragmentRepository = fragmentRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public List<BucketisedMember> addMemberToFragment(Fragment rootFragmentOfView, FragmentationMember member,
													  Observation parentObservation) {
		return fragmentationStrategy.addMemberToFragment(rootFragmentOfView, member, parentObservation);
	}

	@Override
	public List<BucketisedMember> addMemberToBucket(Bucket rootFragmentOfView, FragmentationMember member, Observation parentObservation) {
		return fragmentationStrategy.addMemberToBucket(rootFragmentOfView, member, parentObservation);
	}

	protected void addRelationFromParentToChild(Fragment parentFragment, Fragment childFragment) {
		TreeRelation treeRelation = new TreeRelation("", childFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
		if (!parentFragment.containsRelation(treeRelation)) {
			parentFragment.addRelation(treeRelation);
			fragmentRepository.saveFragment(parentFragment);
		}
	}

	protected void addRelationFromParentToChild(Bucket parentBucket, Bucket childBucket) {
		BucketRelation bucketRelation = BucketRelation.createGenericRelation(parentBucket, childBucket);
		applicationEventPublisher.publishEvent(new BucketRelationCreatedEvent(bucketRelation));
	}

}
