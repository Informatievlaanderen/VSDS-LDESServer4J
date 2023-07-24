package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyExecutorCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationStrategyCollectionImplTest {

	private static final String COLLECTION_NAME = "collectionName";

	private final FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator = mock(
			FragmentationStrategyExecutorCreatorImpl.class);
	private final FragmentationStrategyCreator fragmentationStrategyCreator = mock(FragmentationStrategyCreator.class);
	private final RefragmentationService refragmentationService = mock(RefragmentationService.class);
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private final AllocationRepository allocationRepository = mock(AllocationRepository.class);

	private final FragmentationStrategyCollectionImpl fragmentationStrategyCollection = new FragmentationStrategyCollectionImpl(
			fragmentationStrategyCreator, refragmentationService, fragmentRepository, allocationRepository,
			fragmentationStrategyExecutorCreator);

	@Test
	void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		InitViewAddedResult initResult = initAddView();
		assertTrue(fragmentationStrategyCollection.getViews(COLLECTION_NAME).isEmpty());
		assertTrue(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(initResult.viewSpecification()));

		var executors = fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME);
		assertEquals(1, executors.size());
		assertEquals(initResult.fragmentationStrategyExecutor(), executors.get(0));
		var views = fragmentationStrategyCollection.getViews(COLLECTION_NAME);
		assertEquals(1, views.size());
		assertEquals(initResult.viewName(), views.get(0));
		verify(refragmentationService).refragmentMembersForView(initResult.viewName(), initResult.fragmentationStrategy());
	}

	private InitViewAddedResult initAddView() {
		ViewName viewName = new ViewName(COLLECTION_NAME, "additonalView");
		ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(), List.of());
		FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
				.thenReturn(fragmentationStrategy);
		FragmentationStrategyExecutor fragmentationStrategyExecutor = createFragmentationStrategyExecutor(viewName);
		when(fragmentationStrategyExecutorCreator.createExecutor(viewName, fragmentationStrategy))
				.thenReturn(fragmentationStrategyExecutor);
		return new InitViewAddedResult(viewName, viewSpecification, fragmentationStrategy, fragmentationStrategyExecutor);
	}

	private record InitViewAddedResult(ViewName viewName, ViewSpecification viewSpecification, FragmentationStrategy fragmentationStrategy, FragmentationStrategyExecutor fragmentationStrategyExecutor) {
	}

	private static FragmentationStrategyExecutor createFragmentationStrategyExecutor(ViewName viewName) {
		FragmentationStrategyExecutor fragmentationStrategyExecutor = mock(FragmentationStrategyExecutor.class);
		when(fragmentationStrategyExecutor.isPartOfCollection(COLLECTION_NAME)).thenReturn(true);
		when(fragmentationStrategyExecutor.getViewName()).thenReturn(viewName);
		return fragmentationStrategyExecutor;
	}


	@Test
	void when_ViewDeletedEventIsReceived_FragmentationStrategyIsRemovedFromMap() {
		InitViewAddedResult initResult = initAddView();

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(initResult.viewSpecification()));
		assertFalse(fragmentationStrategyCollection.getViews(COLLECTION_NAME).isEmpty());
		assertFalse(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewDeletedEvent(new ViewDeletedEvent(initResult.viewName()));
		assertTrue(fragmentationStrategyCollection.getViews(COLLECTION_NAME).isEmpty());
		assertTrue(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());
		verify(fragmentRepository).removeLdesFragmentsOfView(initResult.viewSpecification().getName().asString());
		verify(allocationRepository).unallocateAllMembersFromView(initResult.viewSpecification().getName());
	}

//	@Test
//	void when_ViewInitializedEventIsReceived_FragmentationStrategyIsAddedToMap() {
//		ViewSpecification viewSpecification = new ViewSpecification(new ViewName(COLLECTION_NAME, "additonalView"),
//				List.of(), List.of());
//
//		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
//				.thenReturn(mock(FragmentationStrategy.class));
//		assertFalse(
//
//				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
//
//		fragmentationStrategyCollection.handleViewInitializationEvent(new ViewInitializationEvent(viewSpecification));
//
//		assertTrue(
//
//				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
//
//		verify(fragmentationStrategyCreator).createFragmentationStrategyForView(viewSpecification);
//		verifyNoMoreInteractions(fragmentationStrategyCreator, rootFragmentCreator, refragmentationService);
//	}

	@Test
	void should_DeleteTreeNodesByCollection_when_EventStreamDeletedEventIsReceived() {
		String collectionName = "collectionName";

		fragmentationStrategyCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(collectionName));

		verify(allocationRepository).unallocateMembersFromCollection(collectionName);
		verify(fragmentRepository).deleteTreeNodesByCollection(collectionName);
	}

}
