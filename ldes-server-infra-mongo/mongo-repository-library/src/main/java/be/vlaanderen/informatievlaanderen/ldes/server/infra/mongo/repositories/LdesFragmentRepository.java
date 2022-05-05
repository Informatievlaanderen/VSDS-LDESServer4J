package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LdesFragmentRepository extends MongoRepository<LdesFragmentEntity, Integer> {

}
