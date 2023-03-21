package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity.LdesConfigModelEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.repository.LdesConfigEntityRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

public class LdesConfigMongoRepository implements LdesConfigRepository {
	private final LdesConfigEntityRepository repository;
	private final MongoTemplate mongoTemplate;

	public LdesConfigMongoRepository(LdesConfigEntityRepository repository, MongoTemplate mongoTemplate) {
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
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

	private LdesConfigModelEntity getLdesStreamEntity(String collection) {
		return repository.findAllById(collection).orElseThrow(() -> new MissingLdesStreamException(collection));
	}

	@Override
	public LdesConfigModel saveLdesStream(LdesConfigModel ldesConfigModel) {
		repository.save(LdesConfigModelEntity.fromLdesStreamModel(ldesConfigModel));
		return ldesConfigModel;
	}

}
