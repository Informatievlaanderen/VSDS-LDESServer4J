package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.observation.Observation;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {

	private final FragmentationStrategy fragmentationStrategy;

	private final LdesFragmentRepository fragmentRepository;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy,
			LdesFragmentRepository fragmentRepository) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.fragmentRepository = fragmentRepository;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		fragmentationStrategy.addMemberToFragment(parentFragment, member, parentObservation);
	}

	protected void addRelationFromParentToChild(LdesFragment parentFragment, LdesFragment childFragment) {
		TreeRelation treeRelation = new TreeRelation("", childFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
		if (!parentFragment.containsRelation(treeRelation)) {
			parentFragment.addRelation(treeRelation);
			fragmentRepository.saveFragment(parentFragment);
		}
	}

}
