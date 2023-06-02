package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DcatDatasetServiceImpl implements DcatDatasetService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcatDatasetServiceImpl.class);
	private final DcatDatasetRepository repository;

	public DcatDatasetServiceImpl(DcatDatasetRepository repository) {
		this.repository = repository;
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

	@Override
	public void updateDataset(DcatDataset dataset) {
		if (repository.retrieveDataset(dataset.getCollectionName()).isEmpty()) {
			throw new MissingResourceException("dcat-dataset", dataset.getCollectionName());
		}
		repository.saveDataset(dataset);
	}

	@Override
	public void deleteDataset(String id) {
		if (repository.retrieveDataset(id).isEmpty()) {
			LOGGER.warn("No metadata found for collection: {}", id);
		} else {
			repository.deleteDataset(id);
		}
	}
}
