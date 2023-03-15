package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity.LdesStreamEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LdesStreamEntityRepository extends MongoRepository<LdesStreamEntity, String> {

    Optional<LdesStreamEntity> findAllByCollection(String collection);
}
