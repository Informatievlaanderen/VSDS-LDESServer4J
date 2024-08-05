package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.RetentionMemberProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, String> {

	@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
			"FROM members " +
			"WHERE collection_id = :collectionId " +
			"AND subject IN :subjects",
			nativeQuery = true)
	boolean existsByCollectionAndSubjectIn(int collectionId, List<String> subjects);

	List<MemberEntity> findAllByOldIdIn(List<String> oldIds);

	List<MemberEntity> findAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	void deleteAllByOldIdIn(List<String> oldIds);
	void deleteAllByIdIn(List<Long> oldIds);

	void deleteAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	@Query("SELECT m FROM MemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName AND v.eventStream.name = :collectionName AND CAST(m.timestamp as timestamp) < CAST(:timestamp as timestamp)")
	Stream<MemberEntity> findAllByViewNameAndTimestampBefore(String viewName, String collectionName, LocalDateTime timestamp);

	@Query("SELECT m FROM MemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName AND v.eventStream.name = :collectionName")
	Stream<MemberEntity> findAllByViewName(String viewName, String collectionName);

	@Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN (SELECT COUNT(p.id) FROM PageMemberEntity p WHERE p.member.id = m.id) > 0 THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, c.name AS collectionName FROM MemberEntity m JOIN EventStreamEntity c ON m.collection = c WHERE c.name = :collectionName GROUP BY m.id, c.name")
	List<RetentionMemberProjection> findAllByCollectionName(String collectionName);

	@Query("SELECT m.id AS id, m.versionOf AS versionOf, m.timestamp AS timestamp, CASE WHEN (SELECT COUNT(p.id) FROM PageMemberEntity p WHERE p.member.id = m.id) > 0 THEN true ELSE false END AS inView, m.isInEventSource AS inEventSource, c.name AS collectionName FROM MemberEntity m JOIN EventStreamEntity c ON m.collection = c WHERE c.name = :collectionName AND CAST(m.timestamp AS timestamp) < CAST(:timestamp as timestamp) GROUP BY m.id, c.name")
	Stream<RetentionMemberProjection> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime timestamp);
}
