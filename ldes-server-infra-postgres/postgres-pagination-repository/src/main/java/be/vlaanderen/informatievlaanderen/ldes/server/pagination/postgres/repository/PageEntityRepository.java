package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {
	Optional<TreeNodeProjection> findTreeNodeByPartialUrl(String partialUrl);

	@Modifying
	@Query("UPDATE PageEntity p SET p.immutable = true WHERE p.id IN (SELECT r.toPage.id FROM RelationEntity r JOIN r.fromPage p WHERE p.bucket.bucketId = :bucketId)")
	void setAllChildrenImmutableByBucketId(long bucketId);
}