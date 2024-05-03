package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.service.DcatDatasetEntityConverter;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DcatDatasetPostgresRespository implements DcatDatasetRepository {
	private final DcatDatasetEntityRepository repository;
	private final EntityManager entityManager;
	private final DcatDatasetEntityConverter converter = new DcatDatasetEntityConverter();

	public DcatDatasetPostgresRespository(DcatDatasetEntityRepository repository, EntityManager entityManager) {
		this.repository = repository;
		this.entityManager = entityManager;
	}

	@Override
	public Optional<DcatDataset> retrieveDataset(String collectionName) {
		return repository.findById(collectionName).map(converter::entitytoDataset);
	}

	@Override
	public void saveDataset(DcatDataset dataset) {
		entityManager.getTransaction().begin();
		entityManager.persist(converter.datasetToEntity(dataset));
		entityManager.getTransaction().commit();
	}

	@Override
	public void deleteDataset(String id) {
		repository.deleteById(id);
	}

	@Override
	public List<DcatDataset> findAll() {
		return repository.findAll()
				.stream()
				.map(converter::entitytoDataset)
				.toList();
	}
}
