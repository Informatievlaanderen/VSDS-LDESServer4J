package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SnapshotRelationLinkerImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final SnapshotRelationLinker snapshotRelationLinker = new SnapshotRelationLinkerImpl(
			ldesFragmentRepository);

	@Test
	void test_ConnectLastFragmentOfSnapshotToUncoveredFragments() {
		Snapshot snapshot = new Snapshot("id", null, null, null);
		List<LdesFragment> treeNodesOfSnapshot = getTreeNodesOfSnapshot();
		when(ldesFragmentRepository.retrieveFragmentsOfView("id")).thenReturn(treeNodesOfSnapshot);
		List<LdesFragment> treeNodes = getTreeNodes();

		LdesFragment ldesFragment = snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
				treeNodes);

		assertEquals("/id?page=2", ldesFragment.getFragmentId());
		assertEquals(
				List.of(new TreeRelation("", "/uncovered?fragment=1", "", "", GENERIC_TREE_RELATION),
						new TreeRelation("", "/uncovered?fragment=2", "", "", GENERIC_TREE_RELATION)),
				ldesFragment.getRelations());
	}

	@Test
	void when_NoMutableNonRootTreeNodeOfSnapshotIsAvailable_IllegalStateExceptionIsThrown() {
		Snapshot snapshot = new Snapshot("id", null, null, null);
		when(ldesFragmentRepository.retrieveFragmentsOfView("id")).thenReturn(List.of());
		List<LdesFragment> treeNodes = List.of();

		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
						treeNodes));

		assertEquals("Could not find an mutable, non-root treenode of view id", illegalStateException.getMessage());
	}

	private List<LdesFragment> getTreeNodes() {
		LdesFragment rootTreeNode = new LdesFragment("root", List.of());
		LdesFragment coveredTreeNode = new LdesFragment("covered", List.of(new FragmentPair("fragment", "0")));
		coveredTreeNode.makeImmutable();
		return List.of(
				rootTreeNode,
				coveredTreeNode,
				new LdesFragment("uncovered", List.of(new FragmentPair("fragment", "1"))),
				new LdesFragment("uncovered", List.of(new FragmentPair("fragment", "2"))));
	}

	private List<LdesFragment> getTreeNodesOfSnapshot() {
		LdesFragment lastTreeNodeOfSnapshot = new LdesFragment("id", List.of(new FragmentPair("page", "2")));
		LdesFragment rootTreeNodeOfSnapshot = new LdesFragment("id", List.of());
		LdesFragment immutableTreeNodeOfSnapshot = new LdesFragment("id", List.of(new FragmentPair("page", "1")));
		immutableTreeNodeOfSnapshot.makeImmutable();
		return List.of(rootTreeNodeOfSnapshot, immutableTreeNodeOfSnapshot, lastTreeNodeOfSnapshot);
	}

}