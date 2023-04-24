package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;

import java.util.Optional;

public interface SnapshotRepository {

	void saveSnapShot(Snapshot snapshot);

	Optional<Snapshot> getLastSnapshot();
}
