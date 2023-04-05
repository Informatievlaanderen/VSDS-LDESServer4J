package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.springframework.cloud.sleuth.Span;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {

	private final FragmentationStrategy fragmentationStrategy;
	private final LdesFragmentRepository ldesFragmentRepository;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy,
			LdesFragmentRepository ldesFragmentRepository) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, LdesMember ldesMember, Span parentSpan) {
		fragmentationStrategy.addMemberToFragment(parentFragment, ldesMember, parentSpan);
	}

	protected void addRelationFromParentToChild(LdesFragment parentFragment, LdesFragment childFragment) {
		TreeRelation treeRelation = new TreeRelation("", childFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
		if (!parentFragment.getRelations().contains(treeRelation)) {
			parentFragment.addRelation(treeRelation);
			ldesFragmentRepository.saveFragment(parentFragment);
		}
	}

}
