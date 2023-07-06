package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

public interface SnapshotService {
	void createSnapshot(String collectionName);

	void deleteSnapshot(String collectionName);
}
