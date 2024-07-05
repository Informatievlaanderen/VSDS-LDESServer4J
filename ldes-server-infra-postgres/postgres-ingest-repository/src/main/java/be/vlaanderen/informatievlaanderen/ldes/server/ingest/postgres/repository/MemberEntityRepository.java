package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.TreeMemberProjection;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


@Primary
public interface MemberEntityRepository extends JpaRepository<MemberEntity, String> {

	boolean existsByOldIdIn(List<String> oldIds);

	List<MemberEntity> findAllByOldIdIn(List<String> oldIds);

	@Query("SELECT m FROM MemberEntity m LEFT JOIN EventStreamEntity e ON m.collection = e WHERE e.name = :collectionName")
	List<MemberEntity> findAllByCollectionName(String collectionName);

	void deleteAllByOldIdIn(List<String> oldIds);

	@Query("SELECT m.subject AS subject, m.model AS model FROM PageMemberEntity p JOIN p.member m WHERE p.page.id = :pageId")
	List<TreeMemberProjection> findAllByPageId(long pageId);

	@Query("SELECT m.subject AS subject, m.model AS model FROM PageMemberEntity p JOIN p.member m where p.page.partialUrl = :partialUrl")
	List<TreeMemberProjection> findAllByPartialUrl(String partialUrl);
}
