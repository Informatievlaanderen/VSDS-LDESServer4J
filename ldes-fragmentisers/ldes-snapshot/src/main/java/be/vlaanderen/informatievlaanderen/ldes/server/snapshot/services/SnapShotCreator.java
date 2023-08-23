package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Snapshot;

import java.util.List;

public interface SnapShotCreator {

	Snapshot createSnapshotForTreeNodes(List<Fragment> treeNodesForSnapshot,
			String collectionName);
}
