package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("OldShaclShapeEntityRepository")
public interface ShaclShapeEntityRepository extends JpaRepository<ShaclShapeEntity, String> {
}
