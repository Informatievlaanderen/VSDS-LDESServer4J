package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberBucketEntityRepository extends JpaRepository<MemberBucketEntity, String> {
    List<MemberBucketEntity> findAllByViewNameAndSequenceNr(String viewName, Long sequenceNr);
    void deleteAllByViewName (String viewName);
    void deleteAllByViewNameStartingWith (String collectionName);
}
