package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyExecutorCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationStrategyCollectionImplTest {

	private static final String COLLECTION_NAME = "collectionName";

	private final FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator = mock(
			FragmentationStrategyExecutorCreatorImpl.class);
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private final FragmentSequenceRepository fragmentSequenceRepository = mock(FragmentSequenceRepository.class);

	private final FragmentationStrategyCollectionImpl fragmentationStrategyCollection = new FragmentationStrategyCollectionImpl(
			fragmentRepository,
			fragmentationStrategyExecutorCreator, fragmentSequenceRepository);

	@Test
	void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		InitViewAddedResult initResult = initAddView();
		assertTrue(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(initResult.viewSpecification()));

		verifySingleViewAdded(initResult);
		verify(initResult.fragmentationStrategyExecutor()).execute();
	}

	private void verifySingleViewAdded(InitViewAddedResult initResult) {
		var executors = fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME);
		assertEquals(1, executors.size());
		assertEquals(initResult.fragmentationStrategyExecutor(), executors.get(0));
	}

	private InitViewAddedResult initAddView() {
		ViewName viewName = new ViewName(COLLECTION_NAME, "additonalView");
		ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(), List.of(), 100);
		FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);

		FragmentationStrategyExecutor fragmentationStrategyExecutor = createFragmentationStrategyExecutor(viewName);
		when(fragmentationStrategyExecutorCreator.createExecutor(viewName, viewSpecification))
				.thenReturn(fragmentationStrategyExecutor);
		return new InitViewAddedResult(viewName, viewSpecification, fragmentationStrategy,
				fragmentationStrategyExecutor);
	}

	private record InitViewAddedResult(ViewName viewName, ViewSpecification viewSpecification,
			FragmentationStrategy fragmentationStrategy, FragmentationStrategyExecutor fragmentationStrategyExecutor) {
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
		assertFalse(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewDeletedEvent(new ViewDeletedEvent(initResult.viewName()));

		assertTrue(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());
		// verify that the executor is shutdown before removing everything from repos.
		var fragmentationStrategyExecutor = initResult.fragmentationStrategyExecutor;
		InOrder inOrder = inOrder(fragmentRepository, fragmentSequenceRepository, fragmentationStrategyExecutor);
		inOrder.verify(fragmentationStrategyExecutor).shutdown();
		inOrder.verify(fragmentRepository)
				.removeLdesFragmentsOfView(initResult.viewSpecification().getName().asString());
		inOrder.verify(fragmentSequenceRepository).deleteByViewName(initResult.viewName);
	}

	@Test
	void when_ViewInitializedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		InitViewAddedResult initViewAddedResult = initAddView();
		ViewSpecification viewSpecification = initViewAddedResult.viewSpecification;
		assertTrue(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewInitializationEvent(new ViewInitializationEvent(viewSpecification));

		verifySingleViewAdded(initViewAddedResult);
		verify(initViewAddedResult.fragmentationStrategyExecutor()).execute();
	}

	@Test
	void should_DeleteTreeNodesByCollection_when_EventStreamDeletedEventIsReceived() {
		InitViewAddedResult initResult = initAddView();
		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(initResult.viewSpecification()));
		assertFalse(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleEventStreamDeletedEvent(
				new EventStreamDeletedEvent(initResult.viewName.getCollectionName()));

		assertTrue(fragmentationStrategyCollection.getFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());
		// verify that the executor is shutdown before removing everything from repos.
		var fragmentationStrategyExecutor = initResult.fragmentationStrategyExecutor;
		InOrder inOrder = inOrder(fragmentRepository, fragmentSequenceRepository, fragmentationStrategyExecutor);
		inOrder.verify(fragmentationStrategyExecutor).shutdown();
		inOrder.verify(fragmentRepository).deleteTreeNodesByCollection(initResult.viewName.getCollectionName());
		inOrder.verify(fragmentSequenceRepository).deleteByCollection(initResult.viewName.getCollectionName());
	}

}
