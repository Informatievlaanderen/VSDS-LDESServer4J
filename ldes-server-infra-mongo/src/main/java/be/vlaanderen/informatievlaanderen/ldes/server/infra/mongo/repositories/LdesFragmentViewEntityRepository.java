package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LdesFragmentViewEntityRepository extends MongoRepository<LdesFragmentViewEntity, String> {

}
