package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, String> {

	boolean existsByOldIdIn(List<String> oldIds);

	List<MemberEntity> findAllByOldIdIn(List<String> oldIds);

	List<MemberEntity> findAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	@Query("SELECT m FROM MemberEntity m LEFT JOIN EventStreamEntity e ON m.collection = e WHERE e.name = :collectionName")
	List<MemberEntity> findAllByCollectionName(String collectionName);

	void deleteAllByOldIdIn(List<String> oldIds);

	void deleteAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	@Query("SELECT m FROM MemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName AND m.timestamp < :timestamp")
	Stream<MemberEntity> findAllByViewNameAndTimestampBefore(String viewName, LocalDateTime timestamp);

	@Query("SELECT m FROM MemberEntity m JOIN ViewEntity v ON m.collection = v.eventStream WHERE v.name = :viewName")
	Stream<MemberEntity> findAllByViewName(String viewName);

	@Query("SELECT m FROM MemberEntity m WHERE m.collection.name = :collectionName AND m.timestamp < :timestamp")
	Stream<MemberEntity> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime timestamp);
}
