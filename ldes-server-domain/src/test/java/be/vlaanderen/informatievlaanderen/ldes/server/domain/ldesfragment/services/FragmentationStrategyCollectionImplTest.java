package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FragmentationStrategyCollectionImplTest {
	private static final String COLLECTION_NAME = "collectionName";
	private final RootFragmentCreator rootFragmentCreator = mock(RootFragmentCreator.class);
	private final FragmentationStrategyCreator fragmentationStrategyCreator = mock(FragmentationStrategyCreator.class);
	private final RefragmentationService refragmentationService = mock(RefragmentationService.class);
	private final TreeNodeRemover treeNodeRemover = mock(TreeNodeRemover.class);
	private final FragmentationStrategyCollectionImpl fragmentationStrategyCollection = new FragmentationStrategyCollectionImpl(
			rootFragmentCreator, fragmentationStrategyCreator, refragmentationService, treeNodeRemover);

	@Test
	void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName(COLLECTION_NAME, "additonalView"),
				List.of(), List.of());
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
				.thenReturn(mock(FragmentationStrategy.class));
		assertFalse(
				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));

		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(
				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
		verify(rootFragmentCreator).createRootFragmentForView(viewSpecification.getName());
		verify(refragmentationService).refragmentMembersForView(any(), any());
		verify(fragmentationStrategyCreator).createFragmentationStrategyForView(viewSpecification);
	}

	@Test
	void when_ViewDeletedEventIsReceived_FragmentationStrategyIsRemovedFromMap() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName(COLLECTION_NAME, "additonalView"),
				List.of(), List.of());
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
				.thenReturn(mock(FragmentationStrategy.class));
		assertFalse(
				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(
				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
		fragmentationStrategyCollection.handleViewDeletedEvent(new ViewDeletedEvent(viewSpecification.getName()));

		assertFalse(
				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
		verify(treeNodeRemover).removeLdesFragmentsOfView(viewSpecification.getName());
	}

	@Test
	void when_ViewInitializedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName(COLLECTION_NAME, "additonalView"),
				List.of(), List.of());
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
				.thenReturn(mock(FragmentationStrategy.class));
		assertFalse(
				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));

		fragmentationStrategyCollection.handleViewInitializationEvent(new ViewInitializationEvent(viewSpecification));

		assertTrue(
				fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
		verify(fragmentationStrategyCreator).createFragmentationStrategyForView(viewSpecification);
		verifyNoMoreInteractions(fragmentationStrategyCreator, rootFragmentCreator, refragmentationService);
	}
}