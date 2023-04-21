package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.PAGE_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class OpenPageProviderTest {

	private final PageCreator pageCreator = mock(PageCreator.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private OpenPageProvider openPageProvider;
	private static LdesFragment PARENT_FRAGMENT;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(VIEW_NAME, List.of());
		openPageProvider = new OpenPageProvider(pageCreator,
				ldesFragmentRepository, 3L);
	}

	@Test
	@DisplayName("No existing fragment")
	void when_NoFragmentExists_thenFirstFragmentIsCreated() {
		LdesFragment createdFragment = PARENT_FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER,
				"1"));
		when(ldesFragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.empty());
		when(pageCreator.createFirstFragment(PARENT_FRAGMENT))
				.thenReturn(createdFragment);

		Pair<LdesFragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertTrue(ldesFragment.getRight());
		assertEquals(createdFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(ldesFragmentRepository, pageCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verify(pageCreator, times(1)).createFirstFragment(PARENT_FRAGMENT);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(createdFragment);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Incomplete Open Fragment")
	void when_AnIncompleteOpenFragmentExists_thenFragmentIsReturned() {
		LdesFragment existingLdesFragment = PARENT_FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER,
				"2"));
		when(ldesFragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.of(existingLdesFragment));

		Pair<LdesFragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertFalse(ldesFragment.getRight());
		assertEquals(existingLdesFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(ldesFragmentRepository, pageCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Complete Fragment")
	void when_AFullFragmentExists_thenANewFragmentIsCreatedAndReturned() {
		LdesFragment completeFragment = new LdesFragment(
				VIEW_NAME, List.of(new FragmentPair(PAGE_NUMBER,
						"2")),
				false, null, false, 3, List.of());
		LdesFragment newFragment = PARENT_FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER,
				"3"));
		when(ldesFragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.of(completeFragment));
		when(pageCreator.createNewFragment(completeFragment, PARENT_FRAGMENT)).thenReturn(newFragment);

		Pair<LdesFragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertFalse(ldesFragment.getRight());
		assertEquals(newFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(ldesFragmentRepository, pageCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verify(pageCreator, times(1)).createNewFragment(completeFragment, PARENT_FRAGMENT);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
	}
}
