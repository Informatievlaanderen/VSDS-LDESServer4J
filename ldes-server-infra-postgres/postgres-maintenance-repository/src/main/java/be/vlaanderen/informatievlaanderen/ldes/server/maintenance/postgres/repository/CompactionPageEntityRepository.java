package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity.CompactionPageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.projection.CompactionCandidateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CompactionPageEntityRepository extends JpaRepository<CompactionPageEntity, Long> {

	@Query(value = """
			SELECT p.page_id as fragmentId, COUNT(*) AS size, r.to_page_id AS toPage, p.bucket_id AS bucketId, p.partial_url AS partialUrl
			FROM pages p JOIN buckets b ON p.bucket_id = b.bucket_id
			JOIN views v ON b.view_id = v.view_id
			JOIN page_relations r ON p.page_id = r.from_page_id
			JOIN collections c on c.collection_id = v.collection_id
			WHERE c.name = :collectionName
			AND v.name = :viewName
			AND p.expiration IS NULL AND p.immutable
			AND p.partial_url <> CONCAT('/', :collectionName , '/' , :viewName)
			GROUP BY p.page_id, r.to_page_id
			HAVING COUNT(*) < :capacityPerPage
			""", nativeQuery = true)
	@Transactional(readOnly = true)
	List<CompactionCandidateProjection> findCompactionCandidates(@Param("collectionName") String collectionName,
	                                                             @Param("viewName") String viewName,
	                                                             @Param("capacityPerPage") Integer capacityPerPage);

	@Modifying
	@Query("DELETE FROM CompactionPageEntity p WHERE CAST(p.expiration AS timestamp) < CAST(:expiration AS timestamp)")
	void deleteByExpirationBefore(@Param("expiration") LocalDateTime expiration);

	@Modifying
	@Query("UPDATE CompactionPageEntity p SET p.expiration = :expiration WHERE p.id IN :ids")
	void setDeleteTime(@Param("ids") List<Long> ids, @Param("expiration") LocalDateTime expiration);
}