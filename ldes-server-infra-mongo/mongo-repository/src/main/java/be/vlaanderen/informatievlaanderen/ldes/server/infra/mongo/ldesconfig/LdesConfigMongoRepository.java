package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig.entity.LdesConfigModelEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig.repository.LdesConfigEntityRepository;

import java.util.List;
import java.util.Optional;

public class LdesConfigMongoRepository implements LdesConfigRepository {
	private final LdesConfigEntityRepository repository;

	public LdesConfigMongoRepository(LdesConfigEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<LdesConfigModel> retrieveAllConfigModels() {
		return repository
				.findAll()
				.stream().map(LdesConfigModelEntity::toLdesConfigModel)
				.toList();
	}

	@Override
	public Optional<LdesConfigModel> retrieveConfigModel(String collection) {
		return repository
				.findAllById(collection)
				.map(LdesConfigModelEntity::toLdesConfigModel);
	}

	@Override
	public void deleteConfigModel(String collectionName) {
		repository.deleteById(collectionName);
	}

	@Override
	public LdesConfigModel saveConfigModel(LdesConfigModel ldesConfigModel) {
		repository.save(LdesConfigModelEntity.fromLdesConfigModel(ldesConfigModel));
		return ldesConfigModel;
	}

}
