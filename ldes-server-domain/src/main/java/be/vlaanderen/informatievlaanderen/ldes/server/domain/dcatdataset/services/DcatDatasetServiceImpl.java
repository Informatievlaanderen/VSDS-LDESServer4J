package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DcatDatasetServiceImpl implements DcatDatasetService {
	private final DcatDatasetRepository repository;

	public DcatDatasetServiceImpl(DcatDatasetRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<DcatDataset> retrieveDataset(String id) {
		return repository.retrieveDataset(id);
	}

	@Override
	public void saveDataset(DcatDataset dataset) {
		repository.retrieveDataset(dataset.collectionName()).ifPresent(d -> {
			throw new ExistingResourceException("dcat-dataset", dataset.collectionName());
		});
		repository.saveDataset(dataset);
	}
}
