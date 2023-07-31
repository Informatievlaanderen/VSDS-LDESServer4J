package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.SequenceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SequenceEntityRepository extends MongoRepository<SequenceEntity, String> {
}
