package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity.SnapshotEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SnapshotEntityRepository extends MongoRepository<SnapshotEntity, String> {
	void deleteByCollectionName(String collectionName);
}
