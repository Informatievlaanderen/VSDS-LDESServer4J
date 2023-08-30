package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataServiceEntityRepository extends MongoRepository<DataServiceEntity, String> {
}
