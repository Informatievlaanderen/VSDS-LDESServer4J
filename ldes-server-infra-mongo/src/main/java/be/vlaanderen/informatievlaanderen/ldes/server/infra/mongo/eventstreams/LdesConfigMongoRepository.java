package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity.LdesConfigModelEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.repository.LdesConfigEntityRepository;

import java.util.List;
import java.util.Optional;

public class LdesConfigMongoRepository implements LdesConfigRepository {
	private final LdesConfigEntityRepository repository;

	public LdesConfigMongoRepository(LdesConfigEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<LdesConfigModel> retrieveAllLdesStreams() {
		return repository
				.findAll()
				.stream().map(LdesConfigModelEntity::toLdesStreamModel)
				.toList();
	}

	@Override
	public Optional<LdesConfigModel> retrieveLdesStream(String collection) {
		return repository
				.findAllById(collection)
				.map(LdesConfigModelEntity::toLdesStreamModel);
	}

	@Override
	public void deleteLdesStream(String collectionName) {
		repository.deleteById(collectionName);
	}

	@Override
	public LdesConfigModel saveLdesStream(LdesConfigModel ldesConfigModel) {
		repository.save(LdesConfigModelEntity.fromLdesStreamModel(ldesConfigModel));
		return ldesConfigModel;
	}

}
