package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entities.LdesMemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LdesMemberEntityRepository extends MongoRepository<LdesMemberEntity, String> {

}
