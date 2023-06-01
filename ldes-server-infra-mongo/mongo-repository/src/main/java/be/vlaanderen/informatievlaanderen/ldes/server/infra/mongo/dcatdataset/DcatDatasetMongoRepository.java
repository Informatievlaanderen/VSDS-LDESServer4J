package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.service.DcatDatasetEntityConverter;

import java.util.List;
import java.util.Optional;

public class DcatDatasetMongoRepository implements DcatDatasetRepository {

	private final DcatDatasetEntityRepository repository;
	private final DcatDatasetEntityConverter converter = new DcatDatasetEntityConverter();

	public DcatDatasetMongoRepository(DcatDatasetEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<DcatDataset> retrieveDataset(String collectionName) {
		return repository.findById(collectionName).map(converter::entitytoDataset);
	}

	@Override
	public void saveDataset(DcatDataset dataset) {
		repository.save(converter.datasetToEntity(dataset));
	}

	@Override
	public void deleteDataset(String id) {
		repository.deleteById(id);
	}

	// TODO TVB: 1/06/2023 test
	@Override
	public List<DcatDataset> findAll() {
		return repository.findAll().stream().map(converter::entitytoDataset).toList();
	}

}
