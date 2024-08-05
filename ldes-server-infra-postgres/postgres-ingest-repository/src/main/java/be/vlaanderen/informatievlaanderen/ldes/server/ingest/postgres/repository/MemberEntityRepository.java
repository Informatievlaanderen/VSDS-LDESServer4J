package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
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

	@Query("SELECT m FROM MemberEntity m LEFT JOIN EventStreamEntity e ON m.collection = e WHERE e.name = :collectionName")
	List<MemberEntity> findAllByCollectionName(String collectionName);

	void deleteAllByOldIdIn(List<String> oldIds);
	void deleteAllByIdIn(List<Long> oldIds);

	void deleteAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	@Query("SELECT m FROM MemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName AND v.eventStream.name = :collectionName AND CAST(m.timestamp as timestamp) < CAST(:timestamp as timestamp)")
	Stream<MemberEntity> findAllByViewNameAndTimestampBefore(String viewName, String collectionName, LocalDateTime timestamp);

	@Query("SELECT m FROM MemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName AND v.eventStream.name = :collectionName")
	Stream<MemberEntity> findAllByViewName(String viewName, String collectionName);

	@Query("SELECT m FROM MemberEntity m WHERE m.collection.name = :collectionName AND m.timestamp < :timestamp")
	Stream<MemberEntity> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime timestamp);
}
