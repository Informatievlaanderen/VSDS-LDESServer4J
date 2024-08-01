package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.CompactionCandidateProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {
	Optional<TreeNodeProjection> findTreeNodeByPartialUrl(String partialUrl);

	@Modifying
	@Query("UPDATE PageEntity p SET p.immutable = true WHERE p.id IN (SELECT r.toPage.id FROM RelationEntity r JOIN r.fromPage p WHERE p.bucket.bucketId = :bucketId)")
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
            "FROM PageEntity p JOIN BucketEntity b ON p.bucket = b JOIN ViewEntity v ON b.view = v JOIN RelationEntity r ON p = r.fromPage " +
            "WHERE v.eventStream.name = :collectionName AND v.name = :viewName " +
            "GROUP BY p.id, r.toPage.id " +
            "HAVING COUNT(*) < :capacityPerPage")
    List<CompactionCandidateProjection> findCompactionCandidates(@Param("collectionName") String collectionName,
                                                                 @Param("viewName") String viewName,
                                                                 @Param("capacityPerPage") Integer capacityPerPage);

    @Modifying
    @Query("DELETE FROM PageEntity p WHERE p.expiration > :expiration")
    void deleteByExpirationAfter(@Param("expiration") LocalDateTime expiration);
}