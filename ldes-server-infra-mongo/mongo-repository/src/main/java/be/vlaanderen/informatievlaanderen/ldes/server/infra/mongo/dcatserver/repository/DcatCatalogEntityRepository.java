package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.entity.DcatCatalogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DcatCatalogEntityRepository extends MongoRepository<DcatCatalogEntity, String> {
}
