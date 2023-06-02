package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;

import java.util.List;
import java.util.Optional;

public interface DcatDatasetService {

	List<DcatDataset> findAll();

	Optional<DcatDataset> retrieveDataset(String collectionName);

	void saveDataset(DcatDataset dataset);

	void updateDataset(DcatDataset dataset);

	void deleteDataset(String collectionName);
}
