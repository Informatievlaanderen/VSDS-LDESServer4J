package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {
	@Modifying
	@Query(value = "UPDATE pages SET immutable = true WHERE page_id = ? AND NOT is_root", nativeQuery = true)
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