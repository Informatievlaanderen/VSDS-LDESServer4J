package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.mapper.DcatDatasetMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
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
        dcatDatasetEntityRepository.findByCollectionName(dataset.getCollectionName())
                .or(() -> eventStreamEntityRepository.findByName(dataset.getCollectionName()).map(DcatDatasetEntity::new))
                .ifPresent(dcatDatasetEntity -> {
                    dcatDatasetEntity.setModel(dataset.getModel());
                    dcatDatasetEntityRepository.save(dcatDatasetEntity);
                });
    }

    @Override
    @Transactional
    public boolean deleteDataset(String collectionName) {
        final var dcatDatasetEntity = dcatDatasetEntityRepository.findByCollectionName(collectionName);
        if (dcatDatasetEntity.isPresent()) {
            dcatDatasetEntityRepository.delete(dcatDatasetEntity.get());
            return true;
        }
        return false;
    }

    @Override
    public List<DcatDataset> findAll() {
        return dcatDatasetEntityRepository.findAll().stream().map(DcatDatasetMapper::fromEntity).toList();
    }

    @Override
    public boolean exitsByCollectionName(String collectionName) {
        return dcatDatasetEntityRepository.existsByCollectionName(collectionName);
    }
}
