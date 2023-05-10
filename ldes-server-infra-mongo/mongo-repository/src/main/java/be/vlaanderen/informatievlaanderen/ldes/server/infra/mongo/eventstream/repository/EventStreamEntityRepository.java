package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.entity.EventStreamEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventStreamEntityRepository extends MongoRepository<EventStreamEntity, String> {
}
