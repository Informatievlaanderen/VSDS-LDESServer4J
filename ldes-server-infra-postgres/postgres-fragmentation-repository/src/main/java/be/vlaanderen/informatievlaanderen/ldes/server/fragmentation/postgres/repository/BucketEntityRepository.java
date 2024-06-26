package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections.BucketProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BucketEntityRepository extends JpaRepository<BucketEntity, Long> {

	@Query("SELECT b.bucketDescriptor AS bucketDescriptor, CONCAT(b.view.eventStream.name, '/', b.view.name) AS viewName, COUNT(m) AS memberCount " +
			"FROM BucketEntity b LEFT JOIN b.members m " +
			"WHERE b.bucketDescriptor = :bucketDescriptor " +
			"GROUP BY b.bucketDescriptor, b.view.eventStream.name, b.view.name")
	Optional<BucketProjection> findBucketEntityByBucketDescriptor(String bucketDescriptor);

	@Modifying
	@Query(value = """
			WITH view_names AS (SELECT v.view_id, concat(c.name, '/' , v.name) AS view_name FROM views v JOIN collections c ON v.collection_id = c.collection_id)
			INSERT INTO buckets (bucket, view_id) SELECT :bucket, v.view_id FROM view_names v WHERE v.view_name = :viewName
			""", nativeQuery = true)
	void insertBucketEntity(String bucket, String viewName);
}
