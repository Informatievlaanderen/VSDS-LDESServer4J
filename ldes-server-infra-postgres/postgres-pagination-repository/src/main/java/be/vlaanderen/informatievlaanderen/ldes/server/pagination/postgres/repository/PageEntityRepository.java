package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {
	@Transactional(readOnly = true)
	Optional<TreeNodeProjection> findTreeNodeByPartialUrl(String partialUrl);

	@Modifying
	@Query(value = "UPDATE pages SET immutable = true WHERE page_id = ?", nativeQuery = true)
	void setPageImmutable(long pageId);

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
}