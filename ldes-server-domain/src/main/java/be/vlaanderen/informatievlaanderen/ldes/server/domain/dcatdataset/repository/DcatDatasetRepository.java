package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;

import java.util.Optional;

public interface DcatDatasetRepository {

	Optional<DcatDataset> retrieveDataset(String collectionName);

	void saveDataset(DcatDataset dataset);

	void deleteDataset(String id);

}
