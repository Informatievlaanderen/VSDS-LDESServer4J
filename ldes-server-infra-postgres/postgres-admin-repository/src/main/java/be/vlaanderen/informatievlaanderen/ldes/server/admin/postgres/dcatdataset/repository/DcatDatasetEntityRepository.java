package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DcatDatasetEntityRepository extends JpaRepository<DcatDatasetEntity, String> {
}
