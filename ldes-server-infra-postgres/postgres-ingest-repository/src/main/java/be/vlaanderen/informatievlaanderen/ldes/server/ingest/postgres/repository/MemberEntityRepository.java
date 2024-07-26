package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.TreeMemberProjection;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;


@Primary
public interface MemberEntityRepository extends JpaRepository<MemberEntity, String> {

	@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
			"FROM members " +
			"WHERE collection_id = :collectionId " +
			"AND subject IN :subjects",
			nativeQuery = true)
	boolean existsByCollectionAndSubjectIn(int collectionId, List<String> subjects);

	List<MemberEntity> findAllByOldIdIn(List<String> oldIds);

	@Query("SELECT m FROM MemberEntity m LEFT JOIN EventStreamEntity e ON m.collection = e WHERE e.name = :collectionName")
	List<MemberEntity> findAllByCollectionName(String collectionName);

	void deleteAllByOldIdIn(List<String> oldIds);

	@Query("SELECT m.subject AS subject, m.model AS model FROM PageMemberEntity p JOIN p.member m WHERE p.page.id = :pageId")
	List<TreeMemberProjection> findAllByPageId(long pageId);

	@Query("SELECT m.subject AS subject, m.model AS model FROM PageMemberEntity p JOIN p.member m where p.page.partialUrl = :partialUrl")
	List<TreeMemberProjection> findAllByPartialUrl(String partialUrl);

	@Query("SELECT m.member FROM PageMemberEntity m WHERE m.bucket.view.name = :viewName AND m.member.timestamp < :timestamp")
	Stream<MemberEntity> findAllByViewNameAndTimestampBefore(String viewName, LocalDateTime timestamp);

	@Query("SELECT m.member FROM PageMemberEntity m WHERE m.bucket.view.name = :viewName")
	Stream<MemberEntity> findAllByViewName(String viewName);

	@Query("SELECT m FROM MemberEntity m WHERE m.collection.name = :collectionName AND m.timestamp < :timestamp")
	Stream<MemberEntity> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime timestamp);
}
