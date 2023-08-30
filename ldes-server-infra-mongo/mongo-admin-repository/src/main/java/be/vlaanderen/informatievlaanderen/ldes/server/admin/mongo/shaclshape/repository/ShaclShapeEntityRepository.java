package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShaclShapeEntityRepository extends MongoRepository<ShaclShapeEntity, String> {
}
