package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.sleuth.Span;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FragmentationStrategyDecoratorTest {
	FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	LdesFragmentRepository fragmentRepository = mock(LdesFragmentRepository.class);
	private FragmentationStrategyDecorator fragmentationStrategyDecorator;
	private static final String VIEW_NAME = "view";
	private static final String PARENT_FRAGMENT_ID = "parent";
	private static final String CHILD_FRAGMENT_ID = "child";

	@BeforeEach
	void setUp() {
		fragmentationStrategyDecorator = new FragmentationStrategyDecoratorTestImpl(fragmentationStrategy,
				fragmentRepository);
	}

	@Test
	void when_ParentDoesNotYetHaveRelationToChild_AddRelationAndSaveToDatabase() {

		LdesFragment parentFragment = new LdesFragment(PARENT_FRAGMENT_ID,
				new FragmentInfo(VIEW_NAME, List.of()));
		LdesFragment childFragment = new LdesFragment(CHILD_FRAGMENT_ID,
				new FragmentInfo(VIEW_NAME, List.of()));

		fragmentationStrategyDecorator.addRelationFromParentToChild(parentFragment,
				childFragment);

		assertEquals(1, parentFragment.getRelations().size());
		assertEquals(new TreeRelation("", CHILD_FRAGMENT_ID, "", "",
				GENERIC_TREE_RELATION),
				parentFragment.getRelations().get(0));
		Mockito.verify(fragmentRepository,
				Mockito.times(1)).saveFragment(parentFragment);
	}

	@Test
	void when_ParentHasRelationToChild_DoNotAddNewRelation() {
		LdesFragment parentFragment = new LdesFragment(PARENT_FRAGMENT_ID,
				new FragmentInfo(VIEW_NAME, List.of()));
		LdesFragment childFragment = new LdesFragment(CHILD_FRAGMENT_ID,
				new FragmentInfo(VIEW_NAME, List.of()));
		parentFragment.addRelation(new TreeRelation("", CHILD_FRAGMENT_ID, "", "",
				GENERIC_TREE_RELATION));
		fragmentationStrategyDecorator.addRelationFromParentToChild(parentFragment,
				childFragment);

		assertEquals(1, parentFragment.getRelations().size());
		assertEquals(new TreeRelation("", CHILD_FRAGMENT_ID, "", "",
				GENERIC_TREE_RELATION),
				parentFragment.getRelations().get(0));
		Mockito.verifyNoInteractions(fragmentRepository);
	}

	@Test
	void when_DecoratorAddsMemberToFragment_WrappedFragmentationStrategyIsCalled() {
		LdesFragment parentFragment = new LdesFragment(PARENT_FRAGMENT_ID,
				new FragmentInfo(VIEW_NAME, List.of()));
		LdesMember ldesMember = mock(LdesMember.class);
		Span span = mock(Span.class);
		fragmentationStrategyDecorator.addMemberToFragment(parentFragment, ldesMember, span);
		Mockito.verify(fragmentationStrategy,
				Mockito.times(1)).addMemberToFragment(parentFragment, ldesMember, span);
	}

	static class FragmentationStrategyDecoratorTestImpl extends
			FragmentationStrategyDecorator {

		protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy,
				LdesFragmentRepository ldesFragmentRepository) {
			super(fragmentationStrategy, ldesFragmentRepository);
		}
	}
}