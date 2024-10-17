package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity.FetchPageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FetchPageEntityRepository extends JpaRepository<FetchPageEntity, Long> {
	@Transactional(readOnly = true)
	Optional<FetchPageEntity> findTreeNodeByPartialUrl(String partialUrl);
}