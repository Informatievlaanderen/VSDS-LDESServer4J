package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.entity.ShaclShapeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShaclShapeEntityRepository extends MongoRepository<ShaclShapeEntity, String> {
}
