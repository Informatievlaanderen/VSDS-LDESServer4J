package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.TreeRelationsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TreeRelationsEntityRepository extends MongoRepository<TreeRelationsEntity, String> {
}
