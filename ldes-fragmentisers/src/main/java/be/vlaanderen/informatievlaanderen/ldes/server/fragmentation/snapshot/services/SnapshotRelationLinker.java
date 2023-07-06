package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;

import java.util.List;

/**
 * Creates the relation that links the last TreeNode of the Snapshot
 * to the first TreeNode that was not entirely covered by the Snapshot
 */
public interface SnapshotRelationLinker {

	LdesFragment addRelationsToUncoveredTreeNodes(Snapshot snapshot, List<LdesFragment> ldesFragments);
}
