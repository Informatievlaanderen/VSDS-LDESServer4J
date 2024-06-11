package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DcatDatasetEntityRepository extends JpaRepository<DcatDatasetEntity, Integer> {
    @Query("SELECT d FROM DcatDatasetEntity d WHERE d.eventStream.name = :collectionName")
    Optional<DcatDatasetEntity> findByCollectionName(String collectionName);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DcatDatasetEntity d WHERE d.eventStream.name = :collectionName")
    boolean existsByCollectionName(String collectionName);
}
