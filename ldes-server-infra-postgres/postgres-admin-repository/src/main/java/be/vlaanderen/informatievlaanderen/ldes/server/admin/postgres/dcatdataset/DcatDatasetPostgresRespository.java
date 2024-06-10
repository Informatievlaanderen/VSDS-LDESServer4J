package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.service.DcatDatasetEntityConverter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class DcatDatasetPostgresRespository implements DcatDatasetRepository {
	private final DcatDatasetEntityRepository repository;
	private final DcatDatasetEntityConverter converter = new DcatDatasetEntityConverter();

	public DcatDatasetPostgresRespository(DcatDatasetEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<DcatDataset> retrieveDataset(String collectionName) {
		return repository.findById(collectionName).map(converter::entitytoDataset);
	}

	@Override
	@Transactional
	public void saveDataset(DcatDataset dataset) {
		repository.save(converter.datasetToEntity(dataset));
	}

	@Override
	public boolean deleteDataset(String id) {
		repository.deleteById(id);
		return false;
	}

	@Override
	public List<DcatDataset> findAll() {
		return repository.findAll()
				.stream()
				.map(converter::entitytoDataset)
				.toList();
	}

	@Override
	public boolean exitsByCollectionName(String collectionName) {
		return false;
	}
}
