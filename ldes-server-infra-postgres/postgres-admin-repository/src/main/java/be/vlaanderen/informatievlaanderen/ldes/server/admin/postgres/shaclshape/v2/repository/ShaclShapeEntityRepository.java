package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2.entity.ShaclShapeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShaclShapeEntityRepository extends JpaRepository<ShaclShapeEntity, Integer> {
    @Query("SELECT s FROM ShaclShapeEntity s WHERE s.eventStream.name = :collectionName")
    Optional<ShaclShapeEntity> findByCollectionName(String collectionName);

    @Modifying
    @Query("DELETE FROM ShaclShapeEntity s WHERE s.eventStream.name = :collectionName")
    void deleteByCollectionName(String collectionName);

}
