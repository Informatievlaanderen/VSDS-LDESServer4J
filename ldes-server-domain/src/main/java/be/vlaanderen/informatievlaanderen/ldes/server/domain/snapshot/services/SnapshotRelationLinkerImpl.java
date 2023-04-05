package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

/**
 * Creates the relation that links the last TreeNode of the Snapshot
 * to the first TreeNode that was not entirely covered by the Snapshot
 */

@Component
public class SnapshotRelationLinkerImpl implements SnapshotRelationLinker {

	private final LdesFragmentRepository ldesFragmentRepository;

	public SnapshotRelationLinkerImpl(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment addRelationsToUncoveredTreeNodes(Snapshot snapshot, List<LdesFragment> ldesFragments) {
		LdesFragment lastTreeNodeOfSnapshot = getLastTreeNodeOfSnapshot(snapshot);
		List<LdesFragment> uncoveredTreeNodes = getUncoveredTreeNodes(ldesFragments);
		uncoveredTreeNodes.forEach(
				treeNode -> lastTreeNodeOfSnapshot.addRelation(new TreeRelation("", treeNode.getFragmentId(), "", "",
						GENERIC_TREE_RELATION)));
		return lastTreeNodeOfSnapshot;
	}

	private List<LdesFragment> getUncoveredTreeNodes(List<LdesFragment> ldesFragments) {
		return ldesFragments
				.stream()
				.filter(ldesFragment -> !ldesFragment.isImmutable())
				.filter(ldesFragment -> !ldesFragment.isRoot()).toList();
	}

	private LdesFragment getLastTreeNodeOfSnapshot(Snapshot snapshot) {
		return ldesFragmentRepository.retrieveFragmentsOfView(snapshot.getSnapshotId())
				.stream()
				.filter(ldesFragment -> !ldesFragment.isImmutable())
				.filter(ldesFragment -> !ldesFragment.isRoot()).findFirst().orElseThrow(() -> new IllegalStateException(
						"Could not find an mutable, non-root treenode of view " + snapshot.getSnapshotId()));
	}
}
