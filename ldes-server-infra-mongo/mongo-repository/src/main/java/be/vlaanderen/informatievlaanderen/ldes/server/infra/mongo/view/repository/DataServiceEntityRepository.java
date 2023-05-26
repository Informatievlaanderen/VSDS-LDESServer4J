package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.entity.DataServiceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataServiceEntityRepository extends MongoRepository<DataServiceEntity, String> {
}
