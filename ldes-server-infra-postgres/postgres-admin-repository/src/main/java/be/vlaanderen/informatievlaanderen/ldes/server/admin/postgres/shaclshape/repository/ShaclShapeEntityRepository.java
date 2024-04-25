package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShaclShapeEntityRepository extends JpaRepository<ShaclShapeEntity, String> {
}
