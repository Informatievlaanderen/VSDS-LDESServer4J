package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2.mapper.ShaclShapeMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2.repository.ShaclShapeEntityRepository;
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
    public ShaclShape saveShaclShape(ShaclShape shaclShape) {
        eventStreamEntityRepository.findByName(shaclShape.getCollection())
                .map(eventStream -> new ShaclShapeEntity(eventStream, shaclShape.getModel()))
                .ifPresent(shaclShapeEntityRepository::save);
        return shaclShape;
    }

    @Override
    public void deleteShaclShape(String collectionName) {
        shaclShapeEntityRepository.deleteByCollectionName(collectionName);
    }
}
