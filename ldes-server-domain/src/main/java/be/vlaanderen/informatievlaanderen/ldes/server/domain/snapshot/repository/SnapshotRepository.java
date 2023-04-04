package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;

public interface SnapshotRepository {

	void saveSnapShot(Snapshot snapshot);
}
