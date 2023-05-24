package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.entity.ServerDcatEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServerDcatEntityRepository extends MongoRepository<ServerDcatEntity, String> {
}
