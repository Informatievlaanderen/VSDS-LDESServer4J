package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.List;

/**
 * Creates the relation that links the last TreeNode of the Snapshot
 * to the first TreeNode that was not entirely covered by the Snapshot
 */
public interface SnapshotRelationLinker {

	Fragment addRelationsToUncoveredTreeNodes(Snapshot snapshot, List<Fragment> fragments);
}
