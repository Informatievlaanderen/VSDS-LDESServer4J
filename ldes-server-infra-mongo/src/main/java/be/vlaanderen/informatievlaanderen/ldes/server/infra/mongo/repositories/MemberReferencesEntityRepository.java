package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.MemberReferencesEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberReferencesEntityRepository extends MongoRepository<MemberReferencesEntity, String> {

}
