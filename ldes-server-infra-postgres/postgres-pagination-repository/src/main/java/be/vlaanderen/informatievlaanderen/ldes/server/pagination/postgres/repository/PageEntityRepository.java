package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {
	boolean existsByPartialUrl(String partialUrl);

	@Modifying
	@Query(value = "INSERT INTO pages (bucket_id, expiration, partial_url)  VALUES (:bucketId, :expiration, :partialUrl) RETURNING *", nativeQuery = true)
	PageEntity insert(long bucketId, LocalDateTime expiration, String partialUrl);
}
