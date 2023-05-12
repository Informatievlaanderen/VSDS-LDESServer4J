package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;

import java.util.List;

public interface SnapShotCreator {

	Snapshot createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot,
			String collectionName);
}
