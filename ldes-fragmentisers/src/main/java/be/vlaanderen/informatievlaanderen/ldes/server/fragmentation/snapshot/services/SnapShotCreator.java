package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;

import java.util.List;

public interface SnapShotCreator {

	Snapshot createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot,
			String collectionName);
}
