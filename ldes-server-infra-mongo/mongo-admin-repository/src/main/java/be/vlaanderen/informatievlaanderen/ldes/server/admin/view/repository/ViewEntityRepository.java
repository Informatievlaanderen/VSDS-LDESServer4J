package be.vlaanderen.informatievlaanderen.ldes.server.admin.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.view.entity.ViewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ViewEntityRepository extends MongoRepository<ViewEntity, String> {
}
