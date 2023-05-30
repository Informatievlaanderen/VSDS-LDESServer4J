package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;

import java.util.Optional;

public interface DcatDatasetService {
	Optional<DcatDataset> retrieveDataset(String id);

	void saveDataset(DcatDataset dataset);
}
