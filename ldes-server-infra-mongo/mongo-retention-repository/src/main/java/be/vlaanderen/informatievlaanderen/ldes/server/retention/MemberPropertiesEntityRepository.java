package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberPropertiesEntityRepository extends MongoRepository<MemberPropertiesEntity, String> {

}
