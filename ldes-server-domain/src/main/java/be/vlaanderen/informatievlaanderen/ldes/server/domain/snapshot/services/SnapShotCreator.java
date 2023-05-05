package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;

import java.util.List;

public interface SnapShotCreator {

	Snapshot createSnapshotForTreeNodes(List<LdesFragment> treeNodesForSnapshot,
			EventStream eventStream, ShaclShape shape);
}
