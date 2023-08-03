package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyExecutorCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationStrategyCollectionImplTest {

	private static final String COLLECTION_NAME = "collectionName";

	private final FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator = mock(
			FragmentationStrategyExecutorCreatorImpl.class);
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);

	private final FragmentationStrategyCollectionImpl fragmentationStrategyCollection = new FragmentationStrategyCollectionImpl(
			fragmentRepository,
			fragmentationStrategyExecutorCreator);

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
		ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(), List.of(), pageSize);
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
		verify(fragmentRepository).removeLdesFragmentsOfView(initResult.viewSpecification().getName().asString());
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
		String collectionName = "collectionName";

		fragmentationStrategyCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(collectionName));

		verify(fragmentRepository).deleteTreeNodesByCollection(collectionName);
	}

}
