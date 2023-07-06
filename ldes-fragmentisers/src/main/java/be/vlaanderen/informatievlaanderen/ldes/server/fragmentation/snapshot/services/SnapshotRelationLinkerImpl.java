package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.exception.SnapshotCreationException;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

/**
 * Creates the relation that links the last TreeNode of the Snapshot
 * to the first TreeNode that was not entirely covered by the Snapshot
 */

@Component
public class SnapshotRelationLinkerImpl implements SnapshotRelationLinker {

	private final FragmentRepository fragmentRepository;

	public SnapshotRelationLinkerImpl(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
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
				.filter(ldesFragment -> !ldesFragment.isRoot())
				.filter(ldesFragment -> !ldesFragment.isImmutable())
				.filter(ldesFragment -> ldesFragment.getRelations().isEmpty()).toList();
	}

	private LdesFragment getLastTreeNodeOfSnapshot(Snapshot snapshot) {
		return fragmentRepository.retrieveFragmentsOfView(snapshot.getSnapshotId())
				.filter(ldesFragment -> !ldesFragment.isImmutable())
				.filter(ldesFragment -> !ldesFragment.isRoot()).findFirst()
				.orElseThrow(() -> new SnapshotCreationException(
						"Could not find an mutable, non-root treenode of view " + snapshot.getSnapshotId()));
	}
}
