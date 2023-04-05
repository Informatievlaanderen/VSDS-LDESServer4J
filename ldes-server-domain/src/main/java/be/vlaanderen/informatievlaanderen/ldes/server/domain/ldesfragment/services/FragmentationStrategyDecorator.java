package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.springframework.cloud.sleuth.Span;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {

	private final FragmentationStrategy fragmentationStrategy;
	private final TreeRelationsRepository treeRelationsRepository;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy,
			TreeRelationsRepository treeRelationsRepository) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.treeRelationsRepository = treeRelationsRepository;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Span parentSpan) {
		fragmentationStrategy.addMemberToFragment(parentFragment, member, parentSpan);
	}

	protected void addRelationFromParentToChild(LdesFragment parentFragment, LdesFragment childFragment) {
		TreeRelation treeRelation = new TreeRelation("", childFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
		treeRelationsRepository.addTreeRelation(parentFragment.getFragmentId(), treeRelation);
	}

}
