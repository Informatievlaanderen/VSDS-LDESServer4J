package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MemberBucketisationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BucketisationRepository extends MongoRepository<MemberBucketisationEntity, String> {
    List<BucketisedMember> findAllByViewNameAndSequenceNr(String string, Long sequenceNr);
}
