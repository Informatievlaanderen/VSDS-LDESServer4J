package be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatserver.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatserver.entity.DcatCatalogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DcatCatalogEntityRepository extends MongoRepository<DcatCatalogEntity, String> {
}
