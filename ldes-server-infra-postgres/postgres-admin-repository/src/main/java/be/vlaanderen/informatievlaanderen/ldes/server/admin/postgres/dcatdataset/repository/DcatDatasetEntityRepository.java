package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface DcatDatasetEntityRepository extends JpaRepository<DcatDatasetEntity, String> {
}
