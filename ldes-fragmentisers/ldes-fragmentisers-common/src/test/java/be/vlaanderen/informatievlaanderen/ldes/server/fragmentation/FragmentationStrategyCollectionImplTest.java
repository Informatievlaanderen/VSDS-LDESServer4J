// package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreator;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreator;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
// import io.micrometer.observation.ObservationRegistry;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
//
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.*;
//
// class FragmentationStrategyCollectionImplTest {
// private static final String COLLECTION_NAME = "collectionName";
// private final RootFragmentCreator rootFragmentCreator =
// Mockito.mock(RootFragmentCreator.class);
// private final FragmentationStrategyCreator fragmentationStrategyCreator =
// Mockito.mock(
// FragmentationStrategyCreator.class);
// private final RefragmentationService refragmentationService =
// Mockito.mock(RefragmentationService.class);
// private final FragmentRepository fragmentRepository =
// mock(FragmentRepository.class);
// private final MembersToFragmentRepository membersToFragmentRepository =
// mock(MembersToFragmentRepository.class);
// private final ObservationRegistry observationRegistry =
// mock(ObservationRegistry.class);
// private final AllocationRepository allocationRepository =
// mock(AllocationRepository.class);
// private final FragmentationStrategyCollectionImpl
// fragmentationStrategyCollection = new FragmentationStrategyCollectionImpl(
// rootFragmentCreator, fragmentationStrategyCreator, refragmentationService,
// fragmentRepository, allocationRepository, observationRegistry,
// membersToFragmentRepository);
//
// @Test
// void when_ViewAddedEventIsReceived_FragmentationStrategyIsAddedToMap() {
// // TODO TVB: 24/07/23 new set
// // ViewSpecification viewSpecification = new ViewSpecification(new
// // ViewName(COLLECTION_NAME, "additonalView"),
// // List.of(), List.of());
// //
// when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
// // .thenReturn(Mockito.mock(FragmentationStrategy.class));
// // assertFalse(
// //
// fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
// //
// // fragmentationStrategyCollection.handleViewAddedEvent(new
// // ViewAddedEvent(viewSpecification));
// //
// // assertTrue(
// //
// fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
// //
// verify(rootFragmentCreator).createRootFragmentForView(viewSpecification.getName());
// // verify(refragmentationService).refragmentMembersForView(any(), any());
// //
// verify(fragmentationStrategyCreator).createFragmentationStrategyForView(viewSpecification);
// }
//
// @Test
// void when_ViewDeletedEventIsReceived_FragmentationStrategyIsRemovedFromMap()
// {
// // TODO TVB: 24/07/23 impl new set
// // ViewSpecification viewSpecification = new ViewSpecification(new
// // ViewName(COLLECTION_NAME, "additonalView"),
// // List.of(), List.of());
// //
// when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
// // .thenReturn(Mockito.mock(FragmentationStrategy.class));
// // assertFalse(
// //
// fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
// // fragmentationStrategyCollection.handleViewAddedEvent(new
// // ViewAddedEvent(viewSpecification));
// //
// // assertTrue(
// //
// fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
// // fragmentationStrategyCollection.handleViewDeletedEvent(new
// // ViewDeletedEvent(viewSpecification.getName()));
// //
// // assertFalse(
// //
// fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
// //
// verify(fragmentRepository).removeLdesFragmentsOfView(viewSpecification.getName().asString());
// //
// verify(allocationRepository).unallocateAllMembersFromView(viewSpecification.getName());
// }
//
// @Test
// void when_ViewInitializedEventIsReceived_FragmentationStrategyIsAddedToMap()
// {
// // TODO TVB: 24/07/23 impl new set
// // ViewSpecification viewSpecification = new ViewSpecification(new
// // ViewName(COLLECTION_NAME, "additonalView"),
// // List.of(), List.of());
// //
// when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
// // .thenReturn(Mockito.mock(FragmentationStrategy.class));
// // assertFalse(
// //
// fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
// //
// // fragmentationStrategyCollection.handleViewInitializationEvent(new
// // ViewInitializationEvent(viewSpecification));
// //
// // assertTrue(
// //
// fragmentationStrategyCollection.getFragmentationStrategyMap().containsKey(viewSpecification.getName()));
// //
// verify(fragmentationStrategyCreator).createFragmentationStrategyForView(viewSpecification);
// // verifyNoMoreInteractions(fragmentationStrategyCreator,
// rootFragmentCreator,
// // refragmentationService);
// }
//
// @Test
// void
// should_DeleteTreeNodesByCollection_when_EventStreamDeletedEventIsReceived() {
// String collectionName = "collectionName";
//
// fragmentationStrategyCollection.handleEventStreamDeletedEvent(new
// EventStreamDeletedEvent(collectionName));
//
// verify(allocationRepository).unallocateMembersFromCollection(collectionName);
// verify(fragmentRepository).deleteTreeNodesByCollection(collectionName);
// }
//
// }
