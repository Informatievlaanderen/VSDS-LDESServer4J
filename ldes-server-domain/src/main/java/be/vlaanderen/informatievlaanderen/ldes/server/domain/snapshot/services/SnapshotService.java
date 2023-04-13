package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;

public interface SnapshotService {
	void createSnapshot(LdesConfig ldesConfig);
}
