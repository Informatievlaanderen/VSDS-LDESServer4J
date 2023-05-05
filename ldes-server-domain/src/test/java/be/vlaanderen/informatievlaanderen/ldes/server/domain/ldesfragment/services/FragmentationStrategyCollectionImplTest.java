package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationStrategyCollectionImplTest {
	private static final String COLLECTION_NAME = "collectionName";
	private final Map<ViewName, FragmentationStrategy> fragmentationStrategyMap = new HashMap<>();
	private final RootFragmentCreator rootFragmentCreator = mock(RootFragmentCreator.class);
	private final FragmentationStrategyCreator fragmentationStrategyCreator = mock(FragmentationStrategyCreator.class);
	private final RefragmentationService refragmentationService = mock(RefragmentationService.class);
	private final FragmentationStrategyCollectionImpl fragmentationStrategyCollection = new FragmentationStrategyCollectionImpl(
			fragmentationStrategyMap, rootFragmentCreator, fragmentationStrategyCreator, refragmentationService);

	@Test
	void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName(COLLECTION_NAME, "additonalView"),
				List.of(), List.of());
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
				.thenReturn(mock(FragmentationStrategy.class));

		assertFalse(fragmentationStrategyMap.containsKey(viewSpecification.getName()));
		fragmentationStrategyCollection.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(fragmentationStrategyMap.containsKey(viewSpecification.getName()));
		verify(rootFragmentCreator).createRootFragmentForView(viewSpecification.getName());
		verify(refragmentationService).refragmentMembersForView(any(), any());
		verify(fragmentationStrategyCreator).createFragmentationStrategyForView(viewSpecification);

		Map<ViewName, FragmentationStrategy> retrievedFragmentationStrategyMap = fragmentationStrategyCollection
				.getFragmentationStrategyMap();
		assertEquals(retrievedFragmentationStrategyMap, fragmentationStrategyMap);
	}

}