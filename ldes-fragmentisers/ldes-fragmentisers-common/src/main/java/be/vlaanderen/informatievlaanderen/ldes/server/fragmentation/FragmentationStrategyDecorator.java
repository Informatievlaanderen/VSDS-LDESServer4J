package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {

	private final FragmentationStrategy fragmentationStrategy;

	private final FragmentRepository fragmentRepository;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy,
			FragmentRepository fragmentRepository) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.fragmentRepository = fragmentRepository;
	}

	@Override
	public void addMemberToBucket(Fragment rootFragmentOfView, Member member, Observation parentObservation) {
		fragmentationStrategy.addMemberToBucket(rootFragmentOfView, member, parentObservation);
	}

	@Override
	public void saveBucket() {
		fragmentationStrategy.saveBucket();
	}

	protected void addRelationFromParentToChild(Fragment parentFragment, Fragment childFragment) {
		TreeRelation treeRelation = new TreeRelation("", childFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
		if (!parentFragment.containsRelation(treeRelation)) {
			parentFragment.addRelation(treeRelation);
			fragmentRepository.saveFragment(parentFragment);
		}
	}

}
