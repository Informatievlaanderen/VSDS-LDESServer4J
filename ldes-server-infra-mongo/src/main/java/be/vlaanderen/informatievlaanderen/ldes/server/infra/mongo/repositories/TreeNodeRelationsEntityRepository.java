package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.TreeNodeRelationsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TreeNodeRelationsEntityRepository extends MongoRepository<TreeNodeRelationsEntity, String> {
}
