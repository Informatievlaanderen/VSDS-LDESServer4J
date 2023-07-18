package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;

import java.util.Optional;

public interface SnapshotRepository {

	void saveSnapShot(Snapshot snapshot);

	Optional<Snapshot> getLastSnapshot();

	void deleteSnapshotsByCollectionName(String collectionName);
}
