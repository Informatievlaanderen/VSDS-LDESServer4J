package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.List;

public interface SnapShotCreator {

	Snapshot createSnapshotForTreeNodes(List<Fragment> treeNodesForSnapshot,
			String collectionName);
}
