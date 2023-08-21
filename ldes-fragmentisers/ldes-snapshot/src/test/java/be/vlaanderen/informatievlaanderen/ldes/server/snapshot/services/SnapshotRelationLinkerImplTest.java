package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.SnapshotCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class SnapshotRelationLinkerImplTest {

	private LdesFragmentIdentifier lastPageId;
	private final FragmentRepository fragmentRepository = Mockito.mock(FragmentRepository.class);
	private final SnapshotRelationLinker snapshotRelationLinker = new SnapshotRelationLinkerImpl(
			fragmentRepository);

	@BeforeEach
	void setUp() {
		lastPageId = new LdesFragmentIdentifier(new ViewName("collectionName", "id"),
				List.of(new FragmentPair("page", "2")));
	}

	@Test
	void test_ConnectLastFragmentOfSnapshotToUncoveredFragments() {
		Snapshot snapshot = new Snapshot("id", "collectionName", null, null, null);
		Stream<Fragment> treeNodesOfSnapshot = getTreeNodesOfSnapshot();
		when(fragmentRepository.retrieveFragmentsOfView("id")).thenReturn(treeNodesOfSnapshot);
		List<Fragment> treeNodes = getTreeNodes();

		Fragment fragment = snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
				treeNodes);

		assertEquals(lastPageId, fragment.getFragmentId());
		assertEquals(
				List.of(new TreeRelation("",
						LdesFragmentIdentifier.fromFragmentId("/collectionName/uncovered?fragment=1"), "", "",
						GENERIC_TREE_RELATION),
						new TreeRelation("",
								LdesFragmentIdentifier.fromFragmentId("/collectionName/uncovered?fragment=2"), "", "",
								GENERIC_TREE_RELATION)),
				fragment.getRelations());
	}

	@Test
	void when_NoMutableNonRootTreeNodeOfSnapshotIsAvailable_IllegalStateExceptionIsThrown() {
		Snapshot snapshot = new Snapshot("id", "collectionName", null, null, null);
		when(fragmentRepository.retrieveFragmentsOfView("id")).thenReturn(Stream.of());
		List<Fragment> treeNodes = List.of();

		SnapshotCreationException snapshotCreationException = assertThrows(SnapshotCreationException.class,
				() -> snapshotRelationLinker.addRelationsToUncoveredTreeNodes(snapshot,
						treeNodes));

		assertEquals("Unable to create snapshot.\nCause: Could not find an mutable, non-root treenode of view id",
				snapshotCreationException.getMessage());
	}

	private List<Fragment> getTreeNodes() {
		Fragment rootTreeNode = new Fragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "root"), List.of()));
		Fragment coveredTreeNode = new Fragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "covered"),
						List.of(new FragmentPair("fragment", "0"))));
		coveredTreeNode.makeImmutable();
		coveredTreeNode.addRelation(new TreeRelation("", new LdesFragmentIdentifier(new ViewName("", ""), List.of()),
				"", "", GENERIC_TREE_RELATION));
		return List.of(
				rootTreeNode,
				coveredTreeNode,
				new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "uncovered"),
						List.of(new FragmentPair("fragment", "1")))),
				new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "uncovered"),
						List.of(new FragmentPair("fragment", "2")))));
	}

	private Stream<Fragment> getTreeNodesOfSnapshot() {
		Fragment lastTreeNodeOfSnapshot = new Fragment(lastPageId);
		Fragment rootTreeNodeOfSnapshot = new Fragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "id"), List.of()));
		Fragment immutableTreeNodeOfSnapshot = new Fragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "id"),
						List.of(new FragmentPair("page", "1"))));
		immutableTreeNodeOfSnapshot.makeImmutable();
		return Stream.of(rootTreeNodeOfSnapshot, immutableTreeNodeOfSnapshot, lastTreeNodeOfSnapshot);
	}

}
