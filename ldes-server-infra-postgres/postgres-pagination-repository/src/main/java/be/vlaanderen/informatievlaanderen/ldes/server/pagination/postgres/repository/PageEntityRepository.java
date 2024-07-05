package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {
	Optional<TreeNodeProjection> findTreeNodeByPartialUrl(String partialUrl);
}