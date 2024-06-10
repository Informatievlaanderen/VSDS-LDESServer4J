package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.entity.DcatDatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DcatDatasetEntityRepository extends JpaRepository<DcatDatasetEntity, Integer> {
    @Query("SELECT d FROM DcatDatasetEntity d WHERE d.eventStream.name = :collectionName")
    Optional<DcatDatasetEntity> findByCollectionName(String collectionName);

    @Query("DELETE FROM DcatDatasetEntity d WHERE d.eventStream.name = :collectionName")
    void deleteByCollectionName(String collectionName);
}
