package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.entity.EventSourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventSourceEntityRepository extends MongoRepository<EventSourceEntity, String> {
}
