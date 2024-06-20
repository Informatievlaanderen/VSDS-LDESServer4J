package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections.BucketProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BucketEntityRepository extends JpaRepository<BucketEntity, Long> {

	@Query("SELECT b.bucketDescriptor AS bucketDescriptor, CONCAT(b.view.eventStream.name, '/', b.view.name) AS viewName, COUNT(m) AS memberCount " +
			"FROM BucketEntity b LEFT JOIN b.members m " +
			"WHERE b.bucketDescriptor = :bucketDescriptor " +
			"GROUP BY b.bucketDescriptor, b.view.eventStream.name, b.view.name")
	Optional<BucketProjection> findBucketEntityByBucketDescriptor(String bucketDescriptor);
}
