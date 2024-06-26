package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageEntityRepository extends JpaRepository<PageEntity, Long> {

}
