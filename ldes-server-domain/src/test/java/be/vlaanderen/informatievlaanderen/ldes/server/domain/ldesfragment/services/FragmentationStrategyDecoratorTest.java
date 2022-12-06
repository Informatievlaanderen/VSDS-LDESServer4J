package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.sleuth.Span;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FragmentationStrategyDecoratorTest {
	FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	LdesFragmentRepository fragmentRepository = mock(LdesFragmentRepository.class);
	private FragmentationStrategyDecorator fragmentationStrategyDecorator;
	private static final String VIEW_NAME = "view";

	@BeforeEach
	void setUp() {
		fragmentationStrategyDecorator = new FragmentationStrategyDecoratorTestImpl(fragmentationStrategy,
				fragmentRepository);
	}

	@Test
	void when_ParentDoesNotYetHaveRelationToChild_AddRelationAndSaveToDatabase() {

		LdesFragment parentFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		LdesFragment childFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("key", "value"))));

		TreeRelation expectedRelation = new TreeRelation("", childFragment.getFragmentId(), "",
				"", GENERIC_TREE_RELATION);

		// Response from repository due to relation not being present
		when(fragmentRepository.addRelationToFragment(eq(parentFragment), eq(expectedRelation))).thenReturn(true);

		boolean relationAdded = fragmentationStrategyDecorator.addRelationFromParentToChild(parentFragment,
				childFragment);

		verify(fragmentRepository, Mockito.times(1))
				.addRelationToFragment(eq(parentFragment), eq(expectedRelation));
		assertTrue(relationAdded);
	}

	@Test
	void when_ParentHasRelationToChild_DoNotAddNewRelation() {
		LdesFragment parentFragment = new LdesFragment(new FragmentInfo(VIEW_NAME, List.of()));
		LdesFragment childFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("key", "value"))));

		TreeRelation expectedRelation = new TreeRelation("", childFragment.getFragmentId(), "",
				"", GENERIC_TREE_RELATION);

		// Response from repository due to relation already being present
		when(fragmentRepository.addRelationToFragment(eq(parentFragment), eq(expectedRelation))).thenReturn(false);

		boolean relationAdded = fragmentationStrategyDecorator.addRelationFromParentToChild(parentFragment,
				childFragment);

		verify(fragmentRepository, Mockito.times(1))
				.addRelationToFragment(eq(parentFragment), eq(expectedRelation));
		assertFalse(relationAdded);
	}

	@Test
	void when_DecoratorAddsMemberToFragment_WrappedFragmentationStrategyIsCalled() {
		LdesFragment parentFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		Member member = mock(Member.class);
		Span span = mock(Span.class);
		fragmentationStrategyDecorator.addMemberToFragment(parentFragment, member, span);
		Mockito.verify(fragmentationStrategy,
				Mockito.times(1)).addMemberToFragment(parentFragment, member, span);
	}

	static class FragmentationStrategyDecoratorTestImpl extends
			FragmentationStrategyDecorator {

		protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy,
				LdesFragmentRepository ldesFragmentRepository) {
			super(fragmentationStrategy, ldesFragmentRepository);
		}
	}
}