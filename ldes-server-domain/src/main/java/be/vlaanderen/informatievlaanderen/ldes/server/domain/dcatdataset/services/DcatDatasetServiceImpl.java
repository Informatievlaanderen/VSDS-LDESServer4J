package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import org.springframework.stereotype.Service;

@Service
public class DcatDatasetServiceImpl implements DcatDatasetService {
	private final DcatDatasetRepository repository;
	private final DcatDatasetValidator validator;

	public DcatDatasetServiceImpl(DcatDatasetRepository repository, DcatDatasetValidator validator) {
		this.repository = repository;
		this.validator = validator;
	}

	@Override
	public void saveDataset(DcatDataset dataset) {
		validator.validate(dataset);
		repository.retrieveDataset(dataset.id()).ifPresent(d -> {
			throw new ExistingResourceException("dcat-dataset", dataset.id());
		});
		repository.saveDataset(dataset);
	}
}
