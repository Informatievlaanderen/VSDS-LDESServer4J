package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig.DEFAULT_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	}

	@Test
	void when_TreeNodesAreAvailable_TheyCanBeUsedToCreateSnapshot() {
		List<LdesFragment> treeNodesForSnapshot = List.of(new LdesFragment("collectionName", "by-page", List.of()));
		when(ldesFragmentRepository.retrieveFragmentsOfView(DEFAULT_VIEW_NAME)).thenReturn(treeNodesForSnapshot);
		Snapshot snapshot = new Snapshot("id", "collectionName", "shape", LocalDateTime.now(), "of");
		when(snapShotCreator.createSnapshotForTreeNodes(treeNodesForSnapshot, ldesConfig)).thenReturn(snapshot);
		LdesFragment lastTreeNodeOfSnapshot = new LdesFragment("collectionName", "lastTreeNodeOfSnapshot", List.of());
		when(snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot, treeNodesForSnapshot))
				.thenReturn(lastTreeNodeOfSnapshot);

		snapshotService.createSnapshot(ldesConfig);

		InOrder inOrder = inOrder(ldesFragmentRepository, snapShotCreator, snapshotRelationLinker, snapshotRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveFragmentsOfView(DEFAULT_VIEW_NAME);
		inOrder.verify(snapShotCreator, times(1)).createSnapshotForTreeNodes(treeNodesForSnapshot, ldesConfig);
		inOrder.verify(snapshotRelationLinker, times(1)).addRelationsToUncoveredTreeNodes(snapshot,
				treeNodesForSnapshot);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(lastTreeNodeOfSnapshot);
		inOrder.verify(snapshotRepository, times(1)).saveSnapShot(snapshot);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_NoTreeNodesAreAvailable_SnapshotCreationExceptionIsThrown() {
		List<LdesFragment> treeNodesForSnapshot = List.of();
		when(ldesFragmentRepository.retrieveFragmentsOfView(DEFAULT_VIEW_NAME)).thenReturn(treeNodesForSnapshot);

		SnapshotCreationException snapshotCreationException = assertThrows(SnapshotCreationException.class,
				() -> snapshotService.createSnapshot(ldesConfig));

		assertEquals(
				"Unable to create snapshot.\nCause: No TreeNodes available in view by-page which is used for snapshotting",
				snapshotCreationException.getMessage());
	}
}