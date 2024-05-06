package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.service.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RetentionModelSerializer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.Model;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;

public class EventStreamMongoRepository implements EventStreamRepository {
	public static final String ID = "_id";
	public static final String EVENTSOURCE_RETENTION_POLICIES = "retentionPolicies";
	private final EventStreamEntityRepository repository;
	private final EventStreamConverter converter;
	private final RetentionModelSerializer retentionModelSerializer;
	private final MongoTemplate mongoTemplate;

	public EventStreamMongoRepository(EventStreamEntityRepository repository, EventStreamConverter converter,
									  RetentionModelSerializer retentionModelSerializer, MongoTemplate mongoTemplate) {
		this.repository = repository;
        this.converter = converter;
		this.retentionModelSerializer = retentionModelSerializer;
        this.mongoTemplate = mongoTemplate;
    }

	@Override
	public List<EventStream> retrieveAllEventStreams() {
		return repository.findAll()
				.stream()
				.map(converter::toEventStream)
				.toList();
	}

	@Override
	public Optional<EventStream> retrieveEventStream(String collectionName) {
		return repository.findById(collectionName).map(converter::toEventStream);
	}

	@Override
	public EventStream saveEventStream(EventStream eventStream) {
		repository.save(converter.fromEventStream(eventStream));
		return eventStream;
	}

	@Override
	public void deleteEventStream(String collectionName) {
		repository.deleteById(collectionName);
	}

	@Override
	public void saveEventSource(String collectionName, List<Model> retentionPolicies) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ID).is(collectionName));
		Update update = new Update();
		update.set(EVENTSOURCE_RETENTION_POLICIES, retentionModelSerializer.serialize(retentionPolicies));

		mongoTemplate.updateFirst(query, update, EventStreamEntity.class);
	}
}
