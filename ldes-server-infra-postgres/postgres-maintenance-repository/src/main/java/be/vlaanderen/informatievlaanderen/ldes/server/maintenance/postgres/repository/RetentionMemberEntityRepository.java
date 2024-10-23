package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity.RetentionMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.projection.RetentionMemberProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface RetentionMemberEntityRepository extends JpaRepository<RetentionMemberEntity, String> {

    void deleteAllByIdIn(List<Long> ids);
    @Query("SELECT m FROM RetentionMemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream JOIN RetentionPageMemberEntity pm ON pm.memberId = m.id WHERE v.name = :viewName AND v.eventStream.name = :collectionName AND CAST(m.timestamp as timestamp) < CAST(:timestamp as timestamp)")
    Stream<RetentionMemberEntity> findAllByViewNameAndTimestampBefore(String viewName, String collectionName, LocalDateTime timestamp);

    @Query("SELECT m FROM RetentionMemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream JOIN RetentionPageMemberEntity pm ON pm.memberId = m.id WHERE v.name = :viewName AND v.eventStream.name = :collectionName")
    Stream<RetentionMemberEntity> findAllByViewName(String viewName, String collectionName);

    @Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN (SELECT COUNT(p.memberId) FROM RetentionPageMemberEntity p WHERE p.memberId = m.id) > 0 THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, c.name AS collectionName FROM RetentionMemberEntity m JOIN EventStreamEntity c ON m.collection = c WHERE c.name = :collectionName GROUP BY m.id, c.name")
    List<RetentionMemberProjection> findAllByCollectionName(String collectionName);

    @Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN (SELECT COUNT(p.memberId) FROM RetentionPageMemberEntity p WHERE p.memberId = m.id) > 0 THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, c.name AS collectionName FROM RetentionMemberEntity m JOIN EventStreamEntity c ON m.collection = c WHERE c.name = :collectionName AND CAST(m.timestamp AS timestamp) < CAST(:timestamp as timestamp) GROUP BY m.id, c.name")
    List<RetentionMemberProjection> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime timestamp);
}
