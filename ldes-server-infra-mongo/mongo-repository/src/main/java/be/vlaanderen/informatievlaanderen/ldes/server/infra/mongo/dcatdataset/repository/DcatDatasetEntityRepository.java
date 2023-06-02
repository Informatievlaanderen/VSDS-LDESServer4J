package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.entity.DcatDatasetEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DcatDatasetEntityRepository extends MongoRepository<DcatDatasetEntity, String> {
}
