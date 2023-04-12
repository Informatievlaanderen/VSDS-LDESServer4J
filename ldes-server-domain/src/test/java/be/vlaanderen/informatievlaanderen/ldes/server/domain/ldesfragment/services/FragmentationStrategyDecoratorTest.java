package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FragmentationStrategyDecoratorTest {
	FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private FragmentationStrategyDecorator fragmentationStrategyDecorator;
	private static final String VIEW_NAME = "view";

	@BeforeEach
	void setUp() {
		fragmentationStrategyDecorator = new FragmentationStrategyDecoratorTestImpl(fragmentationStrategy,
				ldesFragmentRepository);
	}

	@Test
	void when_ParentDoesNotYetHaveRelationToChild_AddRelationAndSaveToDatabase() {

		LdesFragment parentFragment = new LdesFragment("collectionName", VIEW_NAME, List.of());
		LdesFragment childFragment = parentFragment.createChild(new FragmentPair("key", "value"));
		TreeRelation expectedRelation = new TreeRelation("",
				childFragment.getFragmentId(), "", "",
				GENERIC_TREE_RELATION);

		fragmentationStrategyDecorator.addRelationFromParentToChild(parentFragment,
				childFragment);

		assertTrue(parentFragment.containsRelation(expectedRelation));
		verify(ldesFragmentRepository,
				Mockito.times(1)).saveFragment(parentFragment);
	}

	@Test
	void when_DecoratorAddsMemberToFragment_WrappedFragmentationStrategyIsCalled() {
		LdesFragment parentFragment = new LdesFragment("collectionName", VIEW_NAME, List.of());
		Member member = mock(Member.class);
		Observation span = mock(Observation.class);
		fragmentationStrategyDecorator.addMemberToFragment(parentFragment, member,
				span);
		verify(fragmentationStrategy,
				Mockito.times(1)).addMemberToFragment(parentFragment, member, span);
	}

	static class FragmentationStrategyDecoratorTestImpl extends
			FragmentationStrategyDecorator {
		protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy,
				LdesFragmentRepository ldesFragmentRepository) {
			super(fragmentationStrategy,
					ldesFragmentRepository);
		}
	}
}