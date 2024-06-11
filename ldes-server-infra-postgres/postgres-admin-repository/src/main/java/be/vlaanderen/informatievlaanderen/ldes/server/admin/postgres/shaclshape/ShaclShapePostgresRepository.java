package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.mapper.ShaclShapeMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.repository.ShaclShapeEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShaclShapePostgresRepository implements ShaclShapeRepository {
    private final ShaclShapeEntityRepository shaclShapeEntityRepository;
    private final EventStreamEntityRepository eventStreamEntityRepository;

    public ShaclShapePostgresRepository(ShaclShapeEntityRepository shaclShapeEntityRepository, EventStreamEntityRepository eventStreamEntityRepository) {
        this.shaclShapeEntityRepository = shaclShapeEntityRepository;
        this.eventStreamEntityRepository = eventStreamEntityRepository;
    }

    @Override
    public List<ShaclShape> retrieveAllShaclShapes() {
        return shaclShapeEntityRepository.findAll().stream().map(ShaclShapeMapper::fromEntity).toList();
    }

    @Override
    public Optional<ShaclShape> retrieveShaclShape(String collectionName) {
        return shaclShapeEntityRepository.findByCollectionName(collectionName).map(ShaclShapeMapper::fromEntity);
    }

    @Override
    public void saveShaclShape(ShaclShape shaclShape) {
        shaclShapeEntityRepository.findByCollectionName(shaclShape.getCollection())
                .or(() -> eventStreamEntityRepository.findByName(shaclShape.getCollection()).map(ShaclShapeEntity::new))
                .ifPresent(shaclShapeEntity -> {
                    shaclShapeEntity.setModel(shaclShape.getModel());
                    shaclShapeEntityRepository.save(shaclShapeEntity);
                });
    }

    @Override
    @Transactional
    public void deleteShaclShape(String collectionName) {
        shaclShapeEntityRepository.deleteByCollectionName(collectionName);
    }
}
