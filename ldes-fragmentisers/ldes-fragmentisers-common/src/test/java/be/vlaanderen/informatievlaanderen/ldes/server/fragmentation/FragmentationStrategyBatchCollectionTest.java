package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService.ServiceType.FRAGMENTATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationStrategyBatchCollectionTest {

	private static final String COLLECTION_NAME = "collectionName";
	private final FragmentationStrategyCreator fragmentationStrategyCreator = mock(FragmentationStrategyCreator.class);
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
	private final ObservationRegistry observationRegistry = mock(ObservationRegistry.class);
	private final BucketisedMemberRepository bucketisedMemberRepository = mock(BucketisedMemberRepository.class);
	private final ViewBucketisationService viewBucketisationService = mock(ViewBucketisationService.class);

	private final FragmentationStrategyBatchCollection fragmentationStrategyCollection = new FragmentationStrategyBatchCollection(
			fragmentRepository, bucketisedMemberRepository, fragmentationStrategyCreator, viewBucketisationService, observationRegistry);

	@Test
	void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		InitViewAddedResult initResult = initAddView();
		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(this, initResult.viewSpecification()));

		verifySingleViewAdded(initResult);
		verify(viewBucketisationService).setHasView(initResult.viewName, FRAGMENTATION);
	}

	@Test
	void when_ViewDeletedEventIsReceived_FragmentationStrategyIsRemovedFromMap() {
		InitViewAddedResult initResult = initAddView();
		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(this, initResult.viewSpecification()));
		assertFalse(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewDeletedEvent(new ViewDeletedEvent(this, initResult.viewName()));

		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		verify(viewBucketisationService).setDeletedView(initResult.viewName(), FRAGMENTATION);

		InOrder inOrder = inOrder(fragmentRepository, bucketisedMemberRepository);
		inOrder.verify(fragmentRepository).removeLdesFragmentsOfView(initResult.viewSpecification().getName().asString());
		inOrder.verify(bucketisedMemberRepository).deleteByViewName(initResult.viewName);
	}

	@Test
	void when_ViewInitializedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		InitViewAddedResult initViewAddedResult = initAddView();
		ViewSpecification viewSpecification = initViewAddedResult.viewSpecification;
		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewInitializationEvent(this, viewSpecification));

		verifySingleViewAdded(initViewAddedResult);
		verify(viewBucketisationService).setHasView(initViewAddedResult.viewName, FRAGMENTATION);
	}

	@Test
	void should_DeleteTreeNodesByCollection_when_EventStreamDeletedEventIsReceived() {
		InitViewAddedResult initResult = initAddView();
		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(this, initResult.viewSpecification()));
		assertFalse(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		fragmentationStrategyCollection.handleEventStreamDeletedEvent(
				new EventStreamDeletedEvent(this, initResult.viewName.getCollectionName()));

		assertTrue(fragmentationStrategyCollection.getAllFragmentationStrategyExecutors(COLLECTION_NAME).isEmpty());

		verify(viewBucketisationService).setDeletedCollection(initResult.viewName.getCollectionName(), FRAGMENTATION);

		InOrder inOrder = inOrder(fragmentRepository, bucketisedMemberRepository);
		inOrder.verify(fragmentRepository).deleteTreeNodesByCollection(initResult.viewName.getCollectionName());
		inOrder.verify(bucketisedMemberRepository).deleteByCollection(initResult.viewName.getCollectionName());
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

		final var rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository, observationRegistry);
		FragmentationStrategyBatchExecutor fragmentationStrategyExecutor =
				new FragmentationStrategyBatchExecutor(viewName, fragmentationStrategy, rootFragmentRetriever, observationRegistry);

		return new InitViewAddedResult(viewName, viewSpecification, fragmentationStrategy,
				fragmentationStrategyExecutor);
	}

	private record InitViewAddedResult(ViewName viewName, ViewSpecification viewSpecification,
	                                   FragmentationStrategy fragmentationStrategy, FragmentationStrategyBatchExecutor fragmentationStrategyExecutor) {
	}

}
