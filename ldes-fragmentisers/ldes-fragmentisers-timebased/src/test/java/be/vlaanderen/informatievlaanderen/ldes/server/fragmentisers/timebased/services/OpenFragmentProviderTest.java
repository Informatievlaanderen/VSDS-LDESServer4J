package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		openFragmentProvider = new OpenFragmentProvider(fragmentCreator, ldesFragmentRepository);
	}

	@Test
	@DisplayName("No existing fragment")
	void when_NoFragmentExists_thenFragmentIsCreated() {
		LdesFragment createdFragment = new LdesFragment(
				new FragmentInfo("view", List.of(new FragmentPair("Path",
						"Value"))));
		when(ldesFragmentRepository.retrieveOpenChildFragment(PARENT_FRAGMENT.getFragmentInfo().getViewName(),
				PARENT_FRAGMENT.getFragmentInfo().getFragmentPairs()))
				.thenReturn(Optional.empty());
		when(fragmentCreator.createNewFragment(Optional.empty(), PARENT_FRAGMENT))
				.thenReturn(createdFragment);

		LdesFragment ldesFragment = openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertEquals(createdFragment, ldesFragment);
		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(VIEW_NAME,
						List.of());
		inOrder.verify(fragmentCreator, times(1)).createNewFragment(Optional.empty(),
				PARENT_FRAGMENT);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Incomplete Open Fragment")
	void when_AnIncompleteOpenFragmentExists_thenFragmentIsReturned() {
		LdesFragment existingLdesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("Path",
						"Value"))));
		when(ldesFragmentRepository.retrieveOpenChildFragment(VIEW_NAME,
				List.of()))
				.thenReturn(Optional.of(existingLdesFragment));
		when(fragmentCreator.needsToCreateNewFragment(existingLdesFragment)).thenReturn(false);

		LdesFragment ldesFragment = openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertEquals(existingLdesFragment, ldesFragment);
		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(VIEW_NAME,
						List.of());
		inOrder.verify(fragmentCreator,
				times(1)).needsToCreateNewFragment(existingLdesFragment);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Complete Fragment")
	void when_AFullFragmentExists_thenANewFragmentIsCreatedAndReturned() {
		LdesFragment existingLdesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("Path",
						"Value"))));
		LdesFragment newFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("Path",
						"Value"))));

		when(ldesFragmentRepository.retrieveOpenChildFragment(VIEW_NAME,
				List.of()))
				.thenReturn(Optional.of(existingLdesFragment));
		when(fragmentCreator.needsToCreateNewFragment(existingLdesFragment)).thenReturn(true);
		when(fragmentCreator.createNewFragment(Optional.of(existingLdesFragment),
				PARENT_FRAGMENT)).thenReturn(newFragment);

		LdesFragment ldesFragment = openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);

		assertEquals(newFragment, ldesFragment);
		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(VIEW_NAME,
						List.of());
		inOrder.verify(fragmentCreator,
				times(1)).needsToCreateNewFragment(existingLdesFragment);
		inOrder.verify(fragmentCreator,
				times(1)).createNewFragment(Optional.of(existingLdesFragment), PARENT_FRAGMENT);
		inOrder.verifyNoMoreInteractions();
	}

}