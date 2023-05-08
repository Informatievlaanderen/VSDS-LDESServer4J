package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SnapshotRelationLinkerImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final SnapshotRelationLinker snapshotRelationLinker = new SnapshotRelationLinkerImpl(
			ldesFragmentRepository);

	@Test
	void test_ConnectLastFragmentOfSnapshotToUncoveredFragments() {
		Snapshot snapshot = new Snapshot("id", "collectionName", null, null, null);
		Stream<LdesFragment> treeNodesOfSnapshot = getTreeNodesOfSnapshot();
		when(ldesFragmentRepository.retrieveFragmentsOfView("id")).thenReturn(treeNodesOfSnapshot);
		List<LdesFragment> treeNodes = getTreeNodes();

		LdesFragment ldesFragment = snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
				treeNodes);

		assertEquals("/collectionName/id?page=2", ldesFragment.getFragmentId());
		assertEquals(
				List.of(new TreeRelation("", "/collectionName/uncovered?fragment=1", "", "", GENERIC_TREE_RELATION),
						new TreeRelation("", "/collectionName/uncovered?fragment=2", "", "", GENERIC_TREE_RELATION)),
				ldesFragment.getRelations());
	}

	@Test
	void when_NoMutableNonRootTreeNodeOfSnapshotIsAvailable_IllegalStateExceptionIsThrown() {
		Snapshot snapshot = new Snapshot("id", "collectionName", null, null, null);
		when(ldesFragmentRepository.retrieveFragmentsOfView("id")).thenReturn(Stream.of());
		List<LdesFragment> treeNodes = List.of();

		SnapshotCreationException snapshotCreationException = assertThrows(SnapshotCreationException.class,
				() -> snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
						treeNodes));

		assertEquals("Unable to create snapshot.\nCause: Could not find an mutable, non-root treenode of view id",
				snapshotCreationException.getMessage());
	}

	private List<LdesFragment> getTreeNodes() {
		LdesFragment rootTreeNode = new LdesFragment(new ViewName("collectionName", "root"), List.of());
		LdesFragment coveredTreeNode = new LdesFragment(new ViewName("collectionName", "covered"),
				List.of(new FragmentPair("fragment", "0")));
		coveredTreeNode.makeImmutable();
		coveredTreeNode.addRelation(new TreeRelation("", "", "", "", GENERIC_TREE_RELATION));
		return List.of(
				rootTreeNode,
				coveredTreeNode,
				new LdesFragment(new ViewName("collectionName", "uncovered"),
						List.of(new FragmentPair("fragment", "1"))),
				new LdesFragment(new ViewName("collectionName", "uncovered"),
						List.of(new FragmentPair("fragment", "2"))));
	}

	private Stream<LdesFragment> getTreeNodesOfSnapshot() {
		LdesFragment lastTreeNodeOfSnapshot = new LdesFragment(new ViewName("collectionName", "id"),
				List.of(new FragmentPair("page", "2")));
		LdesFragment rootTreeNodeOfSnapshot = new LdesFragment(new ViewName("collectionName", "id"), List.of());
		LdesFragment immutableTreeNodeOfSnapshot = new LdesFragment(new ViewName("collectionName", "id"),
				List.of(new FragmentPair("page", "1")));
		immutableTreeNodeOfSnapshot.makeImmutable();
		return Stream.of(rootTreeNodeOfSnapshot, immutableTreeNodeOfSnapshot, lastTreeNodeOfSnapshot);
	}

}