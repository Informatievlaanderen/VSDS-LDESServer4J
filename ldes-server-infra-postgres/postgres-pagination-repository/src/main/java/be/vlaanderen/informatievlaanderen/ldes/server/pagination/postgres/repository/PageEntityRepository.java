package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.CompactionCandidateProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {
	@Transactional(readOnly = true)
	Optional<TreeNodeProjection> findTreeNodeByPartialUrl(String partialUrl);

	@Modifying
	@Query(value = "UPDATE pages SET immutable = true WHERE page_id = ?", nativeQuery = true)
	void setPageImmutable(long pageId);

	@Modifying
	@Query(value = """
			update pages set immutable = true
			where page_id in (
			  select distinct r.to_page_id from pages p
			  inner join buckets b on b.bucket_id = p.bucket_id
			  inner join page_relations r on r.from_page_id = p.page_id
			  where b.bucket_id = :bucketId
			)
			""", nativeQuery = true)
	void setAllChildrenImmutableByBucketId(long bucketId);

	@Modifying
	@Query(value = """
			UPDATE pages
			SET immutable = true
			WHERE bucket_id IN (SELECT b.bucket_id
			                    FROM buckets b
			                             JOIN views USING (view_id)
			                             JOIN collections c using (collection_id) WHERE c.name = :collectionName);
			""", nativeQuery = true)
	void markAllPagesImmutableByCollectionName(String collectionName);

	@Query(value = "SELECT p.id as fragmentId, COUNT(*) AS size, r.toPage.id AS toPage, p.immutable AS immutable, " +
	               "p.expiration AS expiration, " +
	               "p.bucket.bucketId AS bucketId, p.partialUrl AS partialUrl " +
	               "FROM PageEntity p JOIN BucketEntity b ON p.bucket = b JOIN ViewEntity v ON b.view = v JOIN PageRelationEntity r ON p = r.fromPage " +
	               "WHERE v.eventStream.name = :collectionName AND v.name = :viewName " +
	               "GROUP BY p.id, r.toPage.id " +
	               "HAVING COUNT(*) < :capacityPerPage")
	@Transactional(readOnly = true)
	List<CompactionCandidateProjection> findCompactionCandidates(@Param("collectionName") String collectionName,
	                                                             @Param("viewName") String viewName,
	                                                             @Param("capacityPerPage") Integer capacityPerPage);

	@Modifying
	@Query("DELETE FROM PageEntity p WHERE CAST(p.expiration AS timestamp) < CAST(:expiration AS timestamp)")
	void deleteByExpirationBefore(@Param("expiration") LocalDateTime expiration);

	@Modifying
	@Query("UPDATE PageEntity p SET p.expiration = :expiration WHERE p.id IN :ids")
	void setDeleteTime(@Param("ids") List<Long> ids, @Param("expiration") LocalDateTime expiration);
}