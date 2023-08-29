package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.repository.SnapshotRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services.SnapshotServiceImpl.DEFAULT_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SnapshotServiceImplTest {
	private static final String COLLECTION_NAME = "collection";
	private final SnapShotCreator snapShotCreator = Mockito.mock(SnapShotCreator.class);
	private final SnapshotRepository snapshotRepository = mock(SnapshotRepository.class);
	private final FragmentRepository fragmentRepository = Mockito.mock(FragmentRepository.class);
	private final SnapshotRelationLinker snapshotRelationLinker = Mockito.mock(SnapshotRelationLinker.class);

	private final SnapshotService snapshotService = new SnapshotServiceImpl(snapShotCreator, fragmentRepository,
			snapshotRepository, snapshotRelationLinker);

	@Nested
	class CollectionConfiguredWithDefaultView {
		@Test
		void when_TreeNodesAreAvailable_TheyCanBeUsedToCreateSnapshot() {
			ViewName viewName = new ViewName(COLLECTION_NAME, DEFAULT_VIEW_NAME);
			List<Fragment> treeNodesForSnapshot = List
					.of(new Fragment(new LdesFragmentIdentifier(viewName, List.of())));
			when(fragmentRepository.retrieveFragmentsOfView(viewName.asString()))
					.thenReturn(treeNodesForSnapshot.stream());
			Snapshot snapshot = new Snapshot("id", COLLECTION_NAME, ModelFactory.createDefaultModel(),
					LocalDateTime.now(),
					"of");
			when(snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, COLLECTION_NAME))
					.thenReturn(snapshot);
			Fragment lastTreeNodeOfSnapshot = new Fragment(
					new LdesFragmentIdentifier(
							new ViewName(COLLECTION_NAME, "lastTreeNodeOfSnapshot"),
							List.of()));
			when(snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot, treeNodesForSnapshot))
					.thenReturn(lastTreeNodeOfSnapshot);

			snapshotService.createSnapshot(COLLECTION_NAME);

			InOrder inOrder = inOrder(fragmentRepository, snapShotCreator, snapshotRelationLinker,
					snapshotRepository);
			inOrder.verify(fragmentRepository, times(1)).retrieveFragmentsOfView(viewName.asString());
			inOrder.verify(snapShotCreator, times(1)).createSnapshotForTreeNodes(treeNodesForSnapshot, COLLECTION_NAME);
			inOrder.verify(snapshotRelationLinker, times(1)).addRelationsToUncoveredTreeNodes(snapshot,
					treeNodesForSnapshot);
			inOrder.verify(fragmentRepository, times(1)).saveFragment(lastTreeNodeOfSnapshot);
			inOrder.verify(snapshotRepository, times(1)).saveSnapShot(snapshot);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_NoTreeNodesAreAvailable_SnapshotCreationExceptionIsThrown() {
			Stream<Fragment> treeNodesForSnapshot = Stream.of();
			when(fragmentRepository.retrieveFragmentsOfView(DEFAULT_VIEW_NAME)).thenReturn(treeNodesForSnapshot);

			SnapshotCreationException snapshotCreationException = assertThrows(SnapshotCreationException.class,
					() -> snapshotService.createSnapshot(COLLECTION_NAME));

			assertEquals(
					"Unable to create snapshot.\nCause: No TreeNodes available in view collection/by-page which is used for snapshotting",
					snapshotCreationException.getMessage());
		}

		@Test
		void when_TreeNodesAreAvailable_And_PreviousSnapshotExists_TheyCanBeUsedToCreateSnapshot() {
			final ViewName defaultViewName = new ViewName(COLLECTION_NAME, DEFAULT_VIEW_NAME);
			final String snapshotName = "snapshot";
			final ViewName snapshotViewName = new ViewName(snapshotName, DEFAULT_VIEW_NAME);

			Fragment rootFragmentOfDefaultView = new Fragment(
					new LdesFragmentIdentifier(defaultViewName, List.of()));
			Fragment fragmentOfDefaultView = rootFragmentOfDefaultView
					.createChild(new FragmentPair("pageNumber", "1"));
			Stream<Fragment> treeNodesFromDefaultView = Stream.of(rootFragmentOfDefaultView, fragmentOfDefaultView);
			when(fragmentRepository.retrieveFragmentsOfView(defaultViewName.asString()))
					.thenReturn(treeNodesFromDefaultView);

			Fragment rootFragmentOfSnapshot = new Fragment(
					new LdesFragmentIdentifier(snapshotViewName, List.of()));
			Fragment fragmentOfSnapshot = rootFragmentOfSnapshot.createChild(new FragmentPair("pageNumber", "1"));
			fragmentOfSnapshot.addRelation(
					new TreeRelation("", fragmentOfDefaultView.getFragmentId(), "", "", GENERIC_TREE_RELATION));

			Stream<Fragment> treeNodesFromPrevSnapshot = Stream
					.of(rootFragmentOfSnapshot, fragmentOfSnapshot);
			when(fragmentRepository.retrieveFragmentsOfView(snapshotViewName.asString()))
					.thenReturn(treeNodesFromPrevSnapshot);

			Optional<Snapshot> lastSnapshot = Optional
					.of(new Snapshot(snapshotViewName.asString(), COLLECTION_NAME, ModelFactory.createDefaultModel(),
							LocalDateTime.now().minusDays(1),
							"of"));
			when(snapshotRepository.getLastSnapshot()).thenReturn(lastSnapshot);
			Snapshot snapshot = new Snapshot("id", COLLECTION_NAME, ModelFactory.createDefaultModel(),
					LocalDateTime.now(),
					"of");
			List<Fragment> treeNodesForSnapshot = List.of(fragmentOfDefaultView, fragmentOfSnapshot);
			ListArgumentMatcher treeNodesForSnapshotArgument = new ListArgumentMatcher(treeNodesForSnapshot);
			when(snapShotCreator.createSnapshotForTreeNodes(argThat(treeNodesForSnapshotArgument), eq(COLLECTION_NAME)))
					.thenReturn(snapshot);
			Fragment lastTreeNodeOfSnapshot = new Fragment(new LdesFragmentIdentifier(
					new ViewName(COLLECTION_NAME, "lastTreeNodeOfSnapshot"),
					List.of()));
			when(snapshotRelationLinker.addRelationsToUncoveredTreeNodes(eq(snapshot),
					argThat(treeNodesForSnapshotArgument)))
					.thenReturn(lastTreeNodeOfSnapshot);

			snapshotService.createSnapshot(COLLECTION_NAME);

			InOrder inOrder = inOrder(fragmentRepository, snapShotCreator, snapshotRelationLinker,
					snapshotRepository);
			inOrder.verify(fragmentRepository, times(1)).retrieveFragmentsOfView(snapshotViewName.asString());
			inOrder.verify(snapShotCreator, times(1)).createSnapshotForTreeNodes(argThat(treeNodesForSnapshotArgument),
					eq(COLLECTION_NAME));
			inOrder.verify(snapshotRelationLinker, times(1)).addRelationsToUncoveredTreeNodes(eq(snapshot),
					argThat(treeNodesForSnapshotArgument));
			inOrder.verify(fragmentRepository, times(1)).saveFragment(lastTreeNodeOfSnapshot);
			inOrder.verify(snapshotRepository, times(1)).saveSnapShot(snapshot);
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Test
	void when_eventStreamIsDeleted_then_deleteSnapshot() {
		final EventStreamDeletedEvent event = new EventStreamDeletedEvent("collection");
		((SnapshotServiceImpl) snapshotService).handleEventStreamDeletedEvent(event);
		verify(snapshotRepository).deleteSnapshotsByCollectionName(event.collectionName());
	}

	private class ListArgumentMatcher implements ArgumentMatcher<List<Fragment>> {

		private final List<Fragment> expectedList;

		public ListArgumentMatcher(List<Fragment> expectedList) {
			this.expectedList = expectedList;
		}

		@Override
		public boolean matches(List<Fragment> actualList) {
			return expectedList.containsAll(actualList) && actualList.containsAll(expectedList);
		}
	}

}
