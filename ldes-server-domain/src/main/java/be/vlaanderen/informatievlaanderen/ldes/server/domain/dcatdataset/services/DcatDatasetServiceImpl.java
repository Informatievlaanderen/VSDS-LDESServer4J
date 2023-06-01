package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DcatDatasetServiceImpl implements DcatDatasetService {
	private final DcatDatasetRepository repository;

	public DcatDatasetServiceImpl(DcatDatasetRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<DcatDataset> findAll() {
		return repository.findAll();
	}

	@Override
	public Optional<DcatDataset> retrieveDataset(String collectionName) {
		return repository.retrieveDataset(collectionName);
	}

	@Override
	public void saveDataset(DcatDataset dataset) {
		repository.retrieveDataset(dataset.getCollectionName()).ifPresent(d -> {
			throw new ExistingResourceException("dcat-dataset", dataset.getCollectionName());
		});
		repository.saveDataset(dataset);
	}
}
