package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity.LdesStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.repository.LdesStreamEntityRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

public class LdesStreamMongoRepository {
    private final LdesStreamEntityRepository repository;
    private final MongoTemplate mongoTemplate;

    public LdesStreamMongoRepository(LdesStreamEntityRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<EventStream> retrieveAllEventStreams() {
        return repository
                .findAll()
                .stream().map(LdesStreamEntity::toEventStream)
                .toList();
    }

    public Optional<EventStream> retrieveEventStream(String collection) {
        return repository
                .findAllByCollection(collection)
                .map(LdesStreamEntity::toEventStream);
    }

    public String retrieveShape(String collection) {
        return repository
                .findAllByCollection(collection)
                .stream()
                .map(LdesStreamEntity::toEventStream)
                .findAny()
                .orElseThrow(() -> new MissingEventStreamException(collection)).shape();
    }

    public List<TreeNode> retrieveViews(String collection) {
        return repository
                .findAllByCollection(collection)
                .stream()
                .map(LdesStreamEntity::toEventStream)
                .findAny()
                .orElseThrow(() -> new MissingEventStreamException(collection)).views();
    }

    public String updateShape(String collection, String shape) {
        LdesStreamEntity ldesStreamEntity = getLdesStreamEntity(collection);
        ldesStreamEntity.setShape(shape);
        repository.save(ldesStreamEntity);

        return ldesStreamEntity.getShape();
    }

    public String addView(String collection, String viewName) {
       LdesStreamEntity ldesStreamEntity = getLdesStreamEntity(collection);
       ldesStreamEntity.getViewNames().add(viewName);
       repository.save(ldesStreamEntity);
       return viewName;
    }

    private LdesStreamEntity getLdesStreamEntity(String collection) {
        return repository.findAllByCollection(collection).orElseThrow(() -> new MissingEventStreamException(collection));
    }

    public EventStream saveEventStream(EventStream eventStream) {
        repository.save(LdesStreamEntity.fromEventStream(eventStream));
        return eventStream;
    }

}
