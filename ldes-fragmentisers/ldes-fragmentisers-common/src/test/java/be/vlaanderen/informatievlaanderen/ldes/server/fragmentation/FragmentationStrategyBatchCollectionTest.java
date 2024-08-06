package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FragmentationStrategyBatchCollectionTest {

	private static final String COLLECTION_NAME = "collectionName";
	private final FragmentationStrategyCreator fragmentationStrategyCreator = mock(FragmentationStrategyCreator.class);
	private final ObservationRegistry observationRegistry = mock(ObservationRegistry.class);
	private final BucketRepository bucketRepository = mock(BucketRepository.class);

	private final FragmentationStrategyBatchCollection fragmentationStrategyCollection = new FragmentationStrategyBatchCollection(
			bucketRepository, fragmentationStrategyCreator, observationRegistry);

	@Test
	void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		InitViewAddedResult initResult = initAddView();
		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(initResult.viewSpecification()));

		verifySingleViewAdded(initResult);
	}

	@Test
	void when_ViewDeletedEventIsReceived_FragmentationStrategyIsRemovedFromMap() {
		InitViewAddedResult initResult = initAddView();
		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(initResult.viewSpecification()));
		assertFalse(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewDeletedEvent(new ViewDeletedEvent(initResult.viewName()));

		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());
	}

	@Test
	void when_ViewInitializedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		InitViewAddedResult initViewAddedResult = initAddView();
		ViewSpecification viewSpecification = initViewAddedResult.viewSpecification;
		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewInitializationEvent(viewSpecification));

		verifySingleViewAdded(initViewAddedResult);
	}

	@Test
	void should_DeleteTreeNodesByCollection_when_EventStreamDeletedEventIsReceived() {
		InitViewAddedResult initResult = initAddView();
		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(initResult.viewSpecification()));
		assertFalse(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleEventStreamDeletedEvent(
				new EventStreamDeletedEvent(initResult.viewName.getCollectionName()));

		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());
	}

	private void verifySingleViewAdded(InitViewAddedResult initResult) {
		var executors = fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME);
		assertEquals(1, executors.size());
		assertEquals(initResult.fragmentationStrategyExecutor(), executors.getFirst());
	}

	private InitViewAddedResult initAddView() {
		ViewName viewName = new ViewName(COLLECTION_NAME, "additonalView");
		ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(), List.of(), 100);
		FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);

		final var rootBucketRetriever = new RootBucketRetriever(viewName, mock(), observationRegistry);
		FragmentationStrategyBatchExecutor fragmentationStrategyExecutor =
				new FragmentationStrategyBatchExecutor(viewName, fragmentationStrategy, rootBucketRetriever, observationRegistry);

		return new InitViewAddedResult(viewName, viewSpecification, fragmentationStrategy,
				fragmentationStrategyExecutor);
	}

	private record InitViewAddedResult(ViewName viewName, ViewSpecification viewSpecification,
	                                   FragmentationStrategy fragmentationStrategy, FragmentationStrategyBatchExecutor fragmentationStrategyExecutor) {
	}

}
