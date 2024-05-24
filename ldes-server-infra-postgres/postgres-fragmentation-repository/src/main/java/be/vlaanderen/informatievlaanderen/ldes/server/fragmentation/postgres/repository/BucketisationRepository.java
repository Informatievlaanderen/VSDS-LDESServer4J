package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketisationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BucketisationRepository extends JpaRepository<MemberBucketisationEntity, String> {
    List<MemberBucketisationEntity> findAllByViewNameAndSequenceNr(String viewName, Long sequenceNr);
}
