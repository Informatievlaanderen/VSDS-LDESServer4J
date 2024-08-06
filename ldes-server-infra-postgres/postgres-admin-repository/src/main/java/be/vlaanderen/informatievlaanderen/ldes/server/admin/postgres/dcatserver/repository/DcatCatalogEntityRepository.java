package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.entity.DcatCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DcatCatalogEntityRepository extends JpaRepository<DcatCatalogEntity, String> {
}
