package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations.SubstringRelationsAttributer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SubstringFragmentFinderTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static LdesFragment PARENT_FRAGMENT;
	private SubstringFragmentCreator substringFragmentCreator;
	private SubstringRelationsAttributer substringRelationsAttributer;
	private SubstringFragmentFinder substringFragmentFinder;

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(
				VIEW_NAME, List.of());
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setMemberLimit(1);
		substringRelationsAttributer = mock(SubstringRelationsAttributer.class);
		substringFragmentCreator = mock(SubstringFragmentCreator.class);
		substringFragmentFinder = new SubstringFragmentFinder(substringFragmentCreator, substringConfig,
				substringRelationsAttributer);
	}

	@Test
	void when_RootFragmentHasNotReachedLimitAndIsInBucket_RootFragmentIsReturned() {
		LdesFragment rootFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "\"\""));

		LdesFragment actualFragment = substringFragmentFinder.getOpenOrLastPossibleFragment(PARENT_FRAGMENT,
				rootFragment, List.of("", "a", "ab"));

		assertEquals(rootFragment, actualFragment);
		InOrder inOrder = inOrder(substringFragmentCreator,
				substringRelationsAttributer);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_RootFragmentHasReachedItsLimit_FirstOpenFragmentIsReturned() {
		LdesFragment rootFragment = new LdesFragment(
				VIEW_NAME, List.of(new FragmentPair(SUBSTRING, "\"\"")), false, 1,
				List.of());
		LdesFragment aFragment = new LdesFragment(
				VIEW_NAME, List.of(new FragmentPair(SUBSTRING, "a")), false, 1,
				List.of());
		LdesFragment abFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "ab"));
		when(substringFragmentCreator.getOrCreateSubstringFragment(PARENT_FRAGMENT,
				"a")).thenReturn(aFragment);
		when(substringFragmentCreator.getOrCreateSubstringFragment(PARENT_FRAGMENT,
				"ab")).thenReturn(abFragment);

		LdesFragment actualFragment = substringFragmentFinder.getOpenOrLastPossibleFragment(PARENT_FRAGMENT,
				rootFragment, List.of("a", "ab"));

		assertEquals(abFragment, actualFragment);

		InOrder inOrder = inOrder(substringFragmentCreator,
				substringRelationsAttributer);
		inOrder.verify(substringFragmentCreator,
				times(1)).getOrCreateSubstringFragment(PARENT_FRAGMENT, "a");
		inOrder.verify(substringRelationsAttributer,
				times(1)).addSubstringRelation(rootFragment, aFragment);
		inOrder.verify(substringFragmentCreator,
				times(1)).getOrCreateSubstringFragment(PARENT_FRAGMENT, "ab");
		inOrder.verify(substringRelationsAttributer,
				times(1)).addSubstringRelation(aFragment, abFragment);
		inOrder.verifyNoMoreInteractions();

	}
}