package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity.LdesStreamModelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LdesStreamEntityRepository extends MongoRepository<LdesStreamModelEntity, String> {

	Optional<LdesStreamModelEntity> findAllById(String collectionName);
}
