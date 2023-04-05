package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.sleuth.Span;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.mockito.Mockito.mock;

class FragmentationStrategyDecoratorTest {
	FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	TreeRelationsRepository treeRelationsRepository = mock(TreeRelationsRepository.class);
	private FragmentationStrategyDecorator fragmentationStrategyDecorator;
	private static final String VIEW_NAME = "view";

	@BeforeEach
	void setUp() {
		fragmentationStrategyDecorator = new FragmentationStrategyDecoratorTestImpl(fragmentationStrategy,
				treeRelationsRepository);
	}

	@Test
	void when_ParentDoesNotYetHaveRelationToChild_AddRelationAndSaveToDatabase() {

		LdesFragment parentFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		LdesFragment childFragment = parentFragment.createChild(new FragmentPair("key", "value"));
		TreeRelation expectedRelation = new TreeRelation("", childFragment.getFragmentId(), "", "",
				GENERIC_TREE_RELATION);

		fragmentationStrategyDecorator.addRelationFromParentToChild(parentFragment,
				childFragment);

		Mockito.verify(treeRelationsRepository,
				Mockito.times(1)).addTreeRelation(parentFragment.getFragmentId(), expectedRelation);
	}

	@Test
	void when_DecoratorAddsMemberToFragment_WrappedFragmentationStrategyIsCalled() {
		LdesFragment parentFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		Member member = mock(Member.class);
		Span span = mock(Span.class);
		fragmentationStrategyDecorator.addMemberToFragment(parentFragment, member,
				span);
		Mockito.verify(fragmentationStrategy,
				Mockito.times(1)).addMemberToFragment(parentFragment, member, span);
	}

	static class FragmentationStrategyDecoratorTestImpl extends
			FragmentationStrategyDecorator {
		protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy,
				TreeRelationsRepository treeRelationsRepository) {
			super(fragmentationStrategy,
					treeRelationsRepository);
		}
	}
}