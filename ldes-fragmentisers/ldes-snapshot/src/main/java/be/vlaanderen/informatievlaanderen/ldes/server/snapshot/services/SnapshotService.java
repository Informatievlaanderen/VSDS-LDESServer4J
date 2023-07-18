package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

public interface SnapshotService {
	void createSnapshot(String collectionName);

	void deleteSnapshot(String collectionName);
}
