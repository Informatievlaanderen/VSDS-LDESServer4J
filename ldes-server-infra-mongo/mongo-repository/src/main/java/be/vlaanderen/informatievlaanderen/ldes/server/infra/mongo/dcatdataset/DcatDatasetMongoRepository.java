package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.repository.DcatDatasetEntityRepository;

import java.util.Optional;

public class DcatDatasetMongoRepository implements DcatDatasetRepository {

	private final DcatDatasetEntityRepository repository;

	public DcatDatasetMongoRepository(DcatDatasetEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<DcatDataset> retrieveDataset(String id) {
		return repository.findById(id).map(DcatDatasetEntity::toDataset);
	}

	@Override
	public void saveDataset(DcatDataset dataset) {
		repository.save(DcatDatasetEntity.fromDataset(dataset));
	}

	@Override
	public void deleteDataset(String id) {
		repository.deleteById(id);
	}
}
