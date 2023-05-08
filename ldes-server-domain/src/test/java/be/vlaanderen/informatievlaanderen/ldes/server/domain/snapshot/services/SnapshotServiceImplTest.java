package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.testServices.ListArgumentMatcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig.DEFAULT_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SnapshotServiceImplTest {
	private final SnapShotCreator snapShotCreator = mock(SnapShotCreator.class);
	private final SnapshotRepository snapshotRepository = mock(SnapshotRepository.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final SnapshotRelationLinker snapshotRelationLinker = mock(SnapshotRelationLinker.class);

	private final SnapshotService snapshotService = new SnapshotServiceImpl(snapShotCreator, ldesFragmentRepository,
			snapshotRepository, snapshotRelationLinker);

	private LdesConfig ldesConfig;

	@BeforeEach
	void setUp() {
		ldesConfig = new LdesConfig();
		ldesConfig.setHostName("localhost:8080");
		ldesConfig.setCollectionName("collection");
		ldesConfig.validation().setShape("shape");
		ldesConfig.setDefaultView(true);
	}

	@Test
	void when_TreeNodesAreAvailable_TheyCanBeUsedToCreateSnapshot() {
		String collectionName = ldesConfig.getCollectionName();
		ViewName viewName = new ViewName(collectionName, DEFAULT_VIEW_NAME);
		List<LdesFragment> treeNodesForSnapshot = List.of(new LdesFragment(viewName, List.of()));
		when(ldesFragmentRepository.retrieveFragmentsOfView(viewName.asString()))
				.thenReturn(treeNodesForSnapshot.stream());
		Snapshot snapshot = new Snapshot("id", collectionName, "shape", LocalDateTime.now(), "of");
		when(snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, ldesConfig)).thenReturn(snapshot);
		LdesFragment lastTreeNodeOfSnapshot = new LdesFragment(new ViewName(collectionName, "lastTreeNodeOfSnapshot"),
				List.of());
		when(snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot, treeNodesForSnapshot))
				.thenReturn(lastTreeNodeOfSnapshot);

		snapshotService.createSnapshot(ldesConfig);

		InOrder inOrder = inOrder(ldesFragmentRepository, snapShotCreator, snapshotRelationLinker, snapshotRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveFragmentsOfView(viewName.asString());
		inOrder.verify(snapShotCreator, times(1)).createSnapshotForTreeNodes(treeNodesForSnapshot, ldesConfig);
		inOrder.verify(snapshotRelationLinker, times(1)).addRelationsToUncoveredTreeNodes(snapshot,
				treeNodesForSnapshot);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(lastTreeNodeOfSnapshot);
		inOrder.verify(snapshotRepository, times(1)).saveSnapShot(snapshot);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_NoTreeNodesAreAvailable_SnapshotCreationExceptionIsThrown() {
		Stream<LdesFragment> treeNodesForSnapshot = Stream.of();
		when(ldesFragmentRepository.retrieveFragmentsOfView(DEFAULT_VIEW_NAME)).thenReturn(treeNodesForSnapshot);

		SnapshotCreationException snapshotCreationException = assertThrows(SnapshotCreationException.class,
				() -> snapshotService.createSnapshot(ldesConfig));

		assertEquals(
				"Unable to create snapshot.\nCause: No TreeNodes available in view collection/by-page which is used for snapshotting",
				snapshotCreationException.getMessage());
	}

	@Test
	void when_TreeNodesAreAvailable_And_PreviousSnapshotExists_TheyCanBeUsedToCreateSnapshot() {
		final String collectionName = "collection";
		final ViewName defaultViewName = new ViewName(collectionName, DEFAULT_VIEW_NAME);
		final String snapshotName = "snapshot";
		final ViewName snapshotViewName = new ViewName(snapshotName, DEFAULT_VIEW_NAME);

		LdesFragment rootFragmentOfDefaultView = new LdesFragment(defaultViewName, List.of());
		LdesFragment fragmentOfDefaultView = rootFragmentOfDefaultView.createChild(new FragmentPair("pageNumber", "1"));
		Stream<LdesFragment> treeNodesFromDefaultView = Stream.of(rootFragmentOfDefaultView, fragmentOfDefaultView);
		when(ldesFragmentRepository.retrieveFragmentsOfView(defaultViewName.asString()))
				.thenReturn(treeNodesFromDefaultView);

		LdesFragment rootFragmentOfSnapshot = new LdesFragment(snapshotViewName, List.of());
		LdesFragment fragmentOfSnapshot = rootFragmentOfSnapshot.createChild(new FragmentPair("pageNumber", "1"));
		fragmentOfSnapshot.addRelation(
				new TreeRelation("", fragmentOfDefaultView.getFragmentId(), "", "", GENERIC_TREE_RELATION));

		Stream<LdesFragment> treeNodesFromPrevSnapshot = Stream
				.of(rootFragmentOfSnapshot, fragmentOfSnapshot);
		when(ldesFragmentRepository.retrieveFragmentsOfView(snapshotViewName.asString()))
				.thenReturn(treeNodesFromPrevSnapshot);

		Optional<Snapshot> lastSnapshot = Optional
				.of(new Snapshot(snapshotViewName.asString(), "shape", collectionName, LocalDateTime.now().minusDays(1),
						"of"));
		when(snapshotRepository.getLastSnapshot()).thenReturn(lastSnapshot);
		Snapshot snapshot = new Snapshot("id", collectionName, "shape", LocalDateTime.now(), "of");
		List<LdesFragment> treeNodesForSnapshot = List.of(fragmentOfDefaultView, fragmentOfSnapshot);
		ListArgumentMatcher treeNodesForSnapshotArgument = new ListArgumentMatcher(treeNodesForSnapshot);
		when(snapShotCreator.createSnapshotForTreeNodes(argThat(treeNodesForSnapshotArgument), eq(ldesConfig)))
				.thenReturn(snapshot);
		LdesFragment lastTreeNodeOfSnapshot = new LdesFragment(new ViewName(collectionName, "lastTreeNodeOfSnapshot"),
				List.of());
		when(snapshotRelationLinker.addRelationsToUncoveredTreeNodes(eq(snapshot),
				argThat(treeNodesForSnapshotArgument)))
				.thenReturn(lastTreeNodeOfSnapshot);

		snapshotService.createSnapshot(ldesConfig);

		InOrder inOrder = inOrder(ldesFragmentRepository, snapShotCreator, snapshotRelationLinker, snapshotRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveFragmentsOfView(snapshotViewName.asString());
		inOrder.verify(snapShotCreator, times(1)).createSnapshotForTreeNodes(argThat(treeNodesForSnapshotArgument),
				eq(ldesConfig));
		inOrder.verify(snapshotRelationLinker, times(1)).addRelationsToUncoveredTreeNodes(eq(snapshot),
				argThat(treeNodesForSnapshotArgument));
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(lastTreeNodeOfSnapshot);
		inOrder.verify(snapshotRepository, times(1)).saveSnapShot(snapshot);
		inOrder.verifyNoMoreInteractions();
	}
}