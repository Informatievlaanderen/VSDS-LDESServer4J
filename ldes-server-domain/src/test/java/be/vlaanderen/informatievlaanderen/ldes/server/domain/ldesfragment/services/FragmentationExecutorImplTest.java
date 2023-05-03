package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationExecutorImplTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, "view");
	private final HashMap<ViewName, FragmentationStrategy> fragmentationMap = new HashMap<>();
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final FragmentationStrategyCreator fragmentationStrategyCreator = mock(FragmentationStrategyCreator.class);
	private FragmentationExecutorImpl fragmentationExecutor;

	@BeforeEach
	void setUp() {

		fragmentationMap.put(VIEW_NAME, fragmentationStrategy);
		fragmentationExecutor = new FragmentationExecutorImpl(fragmentationMap,
				ldesFragmentRepository, ObservationRegistry.create(), fragmentationStrategyCreator);
	}

	@Test
	void when_FragmentExecutionOnMemberIsCalled_RootNodeIsRetrievedAndFragmentationStrategyIsCalled() {
		LdesFragment ldesFragment = new LdesFragment(VIEW_NAME,
				List.of());
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
		verify(fragmentationStrategy, times(1)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void whenMemberIsFromDifferentCollection_thenItIsNotFragmented() {
		final ViewName alternativeViewName = new ViewName("otherCollection", "otherView");
		LdesFragment ldesFragment = new LdesFragment(alternativeViewName, List.of());
		when(ldesFragmentRepository.retrieveRootFragment(alternativeViewName.asString()))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(alternativeViewName.getCollectionName());

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(0))
				.retrieveRootFragment(alternativeViewName.asString());
		verify(fragmentationStrategy, times(0)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void when_RootFragmentDoesNotExist_MissingRootFragmentExceptionIsThrown() {
		when(ldesFragmentRepository
				.retrieveFragment(new LdesFragment(VIEW_NAME,
						List.of()).getFragmentId()))
				.thenReturn(Optional.empty());
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		MissingRootFragmentException missingRootFragmentException = assertThrows(MissingRootFragmentException.class,
				() -> fragmentationExecutor.executeFragmentation(member));

		assertEquals("Could not retrieve root fragment for view collectionName/view",
				missingRootFragmentException.getMessage());
		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
	}

	@Test
	void when_FragmentationExecutorIsCalledInParallel_FragmentationHappensByOneThreadAtATime() {
		LdesFragment ldesFragment = new LdesFragment(VIEW_NAME,
				List.of());
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
				.thenReturn(Optional.of(ldesFragment));
		IntStream.range(0, 100).parallel()
				.forEach(i -> {
					Member member = mock(Member.class);
					when(member.getCollectionName()).thenReturn(COLLECTION_NAME);
					fragmentationExecutor.executeFragmentation(member);
				});

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentationStrategy);
		inOrder.verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
		inOrder.verify(fragmentationStrategy,
				times(100)).addMemberToFragment(eq(ldesFragment),
						any(), any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName(COLLECTION_NAME, "additonalView"),
				List.of(), List.of());

		assertFalse(fragmentationMap.containsKey(viewSpecification.getName()));
		fragmentationExecutor.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(fragmentationMap.containsKey(viewSpecification.getName()));
		verify(fragmentationStrategyCreator).createFragmentationStrategyForView(viewSpecification);
	}

}
