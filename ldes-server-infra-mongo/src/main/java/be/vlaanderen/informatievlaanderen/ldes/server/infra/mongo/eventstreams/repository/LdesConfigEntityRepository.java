package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity.LdesConfigModelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LdesConfigEntityRepository extends MongoRepository<LdesConfigModelEntity, String> {

	Optional<LdesConfigModelEntity> findAllById(String collectionName);
}
