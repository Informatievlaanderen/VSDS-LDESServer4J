package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.mapper.DcatDatasetMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.repository.EventStreamEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
public class DcatDatasetPostgresRepository implements DcatDatasetRepository {
    private final DcatDatasetEntityRepository dcatDatasetEntityRepository;
    private final EventStreamEntityRepository eventStreamEntityRepository;

    public DcatDatasetPostgresRepository(DcatDatasetEntityRepository dcatDatasetEntityRepository, EventStreamEntityRepository eventStreamEntityRepository) {
        this.dcatDatasetEntityRepository = dcatDatasetEntityRepository;
        this.eventStreamEntityRepository = eventStreamEntityRepository;
    }

    @Override
    public Optional<DcatDataset> retrieveDataset(String collectionName) {
        return dcatDatasetEntityRepository.findByCollectionName(collectionName).map(DcatDatasetMapper::fromEntity);
    }

    @Override
    @Transactional
    public void saveDataset(DcatDataset dataset) {
        eventStreamEntityRepository
                .findByName(dataset.getCollectionName())
                .map(eventStream -> new DcatDatasetEntity(eventStream, dataset.getModel()))
                .ifPresent(dcatDatasetEntityRepository::save);
    }

    @Override
    public void deleteDataset(String id) {
        dcatDatasetEntityRepository.deleteByCollectionName(id);
    }

    @Override
    public List<DcatDataset> findAll() {
        return dcatDatasetEntityRepository.findAll().stream().map(DcatDatasetMapper::fromEntity).toList();
    }
}
