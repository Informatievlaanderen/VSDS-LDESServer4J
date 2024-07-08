package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections.BucketProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BucketEntityRepository extends JpaRepository<BucketEntity, Long> {
	@Query("SELECT b.bucketId AS bucketId, b.bucketDescriptor AS bucketDescriptor, CONCAT(b.view.eventStream.name, '/', b.view.name) AS viewName " +
			"FROM BucketEntity b " +
			"WHERE b.bucketDescriptor = :bucketDescriptor AND CONCAT(b.view.eventStream.name, '/', b.view.name) = :viewName")
	Optional<BucketProjection> findBucketEntityByBucketDescriptor(String viewName, String bucketDescriptor);
}
