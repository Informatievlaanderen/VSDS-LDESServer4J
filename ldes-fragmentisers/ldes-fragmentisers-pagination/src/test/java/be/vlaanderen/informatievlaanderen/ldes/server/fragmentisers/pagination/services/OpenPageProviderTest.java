package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.PAGE_NUMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenPageProviderTest {

	private final PageCreator pageCreator = mock(PageCreator.class);
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private OpenPageProvider openPageProvider;
	private static Fragment PARENT_FRAGMENT;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		openPageProvider = new OpenPageProvider(pageCreator,
				fragmentRepository, 3L);
	}

	@Test
	@DisplayName("No existing fragment")
	void when_NoFragmentExists_thenFirstFragmentIsCreated() {
		Fragment createdFragment = PARENT_FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER,
				"1"));
		when(fragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.empty());
		when(pageCreator.createFirstFragment(PARENT_FRAGMENT))
				.thenReturn(createdFragment);

		Pair<Fragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertTrue(ldesFragment.getRight());
		assertEquals(createdFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(fragmentRepository, pageCreator);
		inOrder.verify(fragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verify(pageCreator, times(1)).createFirstFragment(PARENT_FRAGMENT);
		inOrder.verify(fragmentRepository, times(1)).saveFragment(createdFragment);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Incomplete Open Fragment")
	void when_AnIncompleteOpenFragmentExists_thenFragmentIsReturned() {
		Fragment existingFragment = PARENT_FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER,
				"2"));
		when(fragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.of(existingFragment));

		Pair<Fragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertFalse(ldesFragment.getRight());
		assertEquals(existingFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(fragmentRepository, pageCreator);
		inOrder.verify(fragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Complete Fragment")
	void when_AFullFragmentExists_thenANewFragmentIsCreatedAndReturned() {
		Fragment completeFragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(new FragmentPair(PAGE_NUMBER,
						"2"))),
				false, 3, List.of(), null);
		Fragment newFragment = PARENT_FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER,
				"3"));
		when(fragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.of(completeFragment));
		when(pageCreator.createNewFragment(completeFragment, PARENT_FRAGMENT)).thenReturn(newFragment);

		Pair<Fragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertFalse(ldesFragment.getRight());
		assertEquals(newFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(fragmentRepository, pageCreator);
		inOrder.verify(fragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verify(pageCreator, times(1)).createNewFragment(completeFragment, PARENT_FRAGMENT);
		inOrder.verify(fragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
	}
}
