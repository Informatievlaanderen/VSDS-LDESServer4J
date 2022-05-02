package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entity.LdesFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LdesFragmentMongoRepository extends MongoRepository<LdesFragmentEntity, Integer> {

}
