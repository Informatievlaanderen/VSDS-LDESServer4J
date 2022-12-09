package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenFragmentProviderTest {

	private final TimeBasedFragmentCreator fragmentCreator = mock(TimeBasedFragmentCreator.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private OpenFragmentProvider openFragmentProvider;
	private static LdesFragment PARENT_FRAGMENT;
	private static final String VIEW_NAME = "view";

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(new FragmentInfo(VIEW_NAME, List.of()));
		TimebasedFragmentationConfig timebasedFragmentationConfig = createSequentialFragmentationConfig();
		openFragmentProvider = new OpenFragmentProvider(fragmentCreator,
				ldesFragmentRepository, timebasedFragmentationConfig);
	}

	@Test
	@DisplayName("No existing fragment")
	void when_NoFragmentExists_thenFragmentIsCreated() {
		LdesFragment createdFragment = PARENT_FRAGMENT.createChild(new FragmentPair("Path",
				"Value"));
		when(ldesFragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.empty());
		when(fragmentCreator.createNewFragment(PARENT_FRAGMENT))
				.thenReturn(createdFragment);

		Pair<LdesFragment, Boolean> ldesFragment = openFragmentProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertTrue(ldesFragment.getRight());
		assertEquals(createdFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verify(fragmentCreator, times(1)).createNewFragment(PARENT_FRAGMENT);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(createdFragment);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Incomplete Open Fragment")
	void when_AnIncompleteOpenFragmentExists_thenFragmentIsReturned() {
		LdesFragment existingLdesFragment = PARENT_FRAGMENT.createChild(new FragmentPair("Path",
				"Value"));
		when(ldesFragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.of(existingLdesFragment));

		Pair<LdesFragment, Boolean> ldesFragment = openFragmentProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertFalse(ldesFragment.getRight());
		assertEquals(existingLdesFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Complete Fragment")
	void when_AFullFragmentExists_thenANewFragmentIsCreatedAndReturned() {
		LdesFragment completeFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("OldPath",
						"OldValue")), false, null, false, 3));
		LdesFragment newFragment = PARENT_FRAGMENT.createChild(new FragmentPair("Path",
				"Value"));
		when(ldesFragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId()))
				.thenReturn(Optional.of(completeFragment));
		when(fragmentCreator.createNewFragment(completeFragment, PARENT_FRAGMENT)).thenReturn(newFragment);

		Pair<LdesFragment, Boolean> ldesFragment = openFragmentProvider
				.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertFalse(ldesFragment.getRight());
		assertEquals(newFragment, ldesFragment.getKey());
		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentId());
		inOrder.verify(fragmentCreator, times(1)).createNewFragment(completeFragment, PARENT_FRAGMENT);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
	}

	private TimebasedFragmentationConfig createSequentialFragmentationConfig() {
		return new TimebasedFragmentationConfig(3L);
	}

}