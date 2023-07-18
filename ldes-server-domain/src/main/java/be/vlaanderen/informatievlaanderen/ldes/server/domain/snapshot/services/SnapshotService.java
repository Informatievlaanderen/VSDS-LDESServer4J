package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

public interface SnapshotService {
	void createSnapshot(String collectionName);

	void deleteSnapshot(String collectionName);
}
