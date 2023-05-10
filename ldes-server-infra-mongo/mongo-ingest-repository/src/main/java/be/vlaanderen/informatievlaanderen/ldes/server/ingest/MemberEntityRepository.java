package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberEntityRepository extends MongoRepository<MemberEntity, String> {
}
