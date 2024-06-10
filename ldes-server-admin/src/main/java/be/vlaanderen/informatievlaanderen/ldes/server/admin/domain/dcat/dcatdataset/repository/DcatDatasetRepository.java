package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;

import java.util.List;
import java.util.Optional;

public interface DcatDatasetRepository {

	Optional<DcatDataset> retrieveDataset(String collectionName);

	void saveDataset(DcatDataset dataset);

	boolean deleteDataset(String id);

	List<DcatDataset> findAll();

	boolean exitsByCollectionName(String collectionName);
}
