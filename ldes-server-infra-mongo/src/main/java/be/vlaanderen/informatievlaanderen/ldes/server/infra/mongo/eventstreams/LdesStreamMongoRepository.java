package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity.LdesStreamModelEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.repository.LdesStreamEntityRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

public class LdesStreamMongoRepository implements LdesStreamRepository {
	private final LdesStreamEntityRepository repository;
	private final MongoTemplate mongoTemplate;

	public LdesStreamMongoRepository(LdesStreamEntityRepository repository, MongoTemplate mongoTemplate) {
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<LdesStreamModel> retrieveAllLdesStreams() {
		return repository
				.findAll()
				.stream().map(LdesStreamModelEntity::toLdesStreamModel)
				.toList();
	}

	@Override
	public Optional<LdesStreamModel> retrieveLdesStream(String collection) {
		return repository
				.findAllByCollection(collection)
				.map(LdesStreamModelEntity::toLdesStreamModel);
	}

	@Override
    public String retrieveShape(String collection) {
        return repository.findAllByCollection(collection).
    }

	@Override
	public List<TreeNode> retrieveViews(String collection) {

	}

	@Override
	public String updateShape(String collection, String shape) {

	}

	@Override
	public String addView(String collection, String viewName) {

	}

	private LdesStreamModelEntity getLdesStreamEntity(String collection) {
		return repository.findAllByCollection(collection).orElseThrow(() -> new MissingLdesStreamException(collection));
	}

	@Override
	public LdesStreamModel saveLdesStream(LdesStreamModel ldesStreamModel) {
		repository.save(LdesStreamModelEntity.fromLdesStreamModel(ldesStreamModel));
		return ldesStreamModel;
	}

}
