package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatDatasetPostgresRepositoryTest {

    private static final String DATASET_COLLECTION_NAME = "id";
    private static final String MODEL_FILE_PATH = "dcat-dataset/dataset.ttl";
    private DcatDatasetEntity entity;
    private DcatDataset dataset;
    @Mock
    private DcatDatasetEntityRepository dcatDatasetEntityRepository;
    @Mock
    private EventStreamEntityRepository eventStreamEntityRepository;
    @InjectMocks
    private DcatDatasetPostgresRepository repository;

    @BeforeEach
    void setUp() {
        Model model = readDcatDatasetModel();
        dataset = new DcatDataset(DATASET_COLLECTION_NAME, model);
        entity = initializeEntity(model);
    }

    @Test
    void when_DatasetPresent_Then_ReturnDataset() {

        when(dcatDatasetEntityRepository.findByCollectionName(DATASET_COLLECTION_NAME)).thenReturn(Optional.of(entity));

        Optional<DcatDataset> retrievedDataset = repository.retrieveDataset(DATASET_COLLECTION_NAME);

        assertThat(retrievedDataset)
                .hasValueSatisfying(actualDataset -> {
                    assertThat(actualDataset.getCollectionName()).isEqualTo(DATASET_COLLECTION_NAME);
                    assertThat(actualDataset.getModel()).matches(dataset.getModel()::isIsomorphicWith);
                });
    }

    @Test
    void when_DatasetNotPresent_Then_ReturnEmpty() {
        final String otherId = "other";
        when(dcatDatasetEntityRepository.findByCollectionName(otherId)).thenReturn(Optional.empty());

        Optional<DcatDataset> actualDataset = repository.retrieveDataset(otherId);

        assertTrue(actualDataset.isEmpty());
    }

    @Test
    void given_NewDataSet_when_SaveDataSet_then_SaveDcatDatasetEntity() {
        when(dcatDatasetEntityRepository.findByCollectionName(DATASET_COLLECTION_NAME)).thenReturn(Optional.of(entity));

        repository.saveDataset(dataset);

        InOrder inOrder = inOrder(dcatDatasetEntityRepository, eventStreamEntityRepository);
        inOrder.verify(dcatDatasetEntityRepository).findByCollectionName(DATASET_COLLECTION_NAME);
        inOrder.verify(dcatDatasetEntityRepository).save(any(DcatDatasetEntity.class));
    }

    @Test
    void given_ExistingDataSet_when_SaveDataSet_then_SaveDcatDatasetEntity() {
        when(dcatDatasetEntityRepository.findByCollectionName(DATASET_COLLECTION_NAME)).thenReturn(Optional.empty());
        when(eventStreamEntityRepository.findByName(DATASET_COLLECTION_NAME)).thenReturn(Optional.of(mock()));

        repository.saveDataset(dataset);

        InOrder inOrder = inOrder(dcatDatasetEntityRepository, eventStreamEntityRepository);
        inOrder.verify(dcatDatasetEntityRepository).findByCollectionName(DATASET_COLLECTION_NAME);
        inOrder.verify(eventStreamEntityRepository).findByName(DATASET_COLLECTION_NAME);
        inOrder.verify(dcatDatasetEntityRepository).save(any(DcatDatasetEntity.class));
    }

    @Test
    void when_DatasetPresent_Then_DatasetRemoved() {
        when(dcatDatasetEntityRepository.findByCollectionName(DATASET_COLLECTION_NAME)).thenReturn(Optional.of(entity));

        repository.deleteDataset(DATASET_COLLECTION_NAME);

        verify(dcatDatasetEntityRepository).delete(entity);
    }

    @Nested
    class FindAll {
        @Test
        void when_NoEntitiesAreFound_then_AnEmptyListIsReturned() {
            when(dcatDatasetEntityRepository.findAll()).thenReturn(new ArrayList<>());

            List<DcatDataset> result = repository.findAll();

            assertTrue(result.isEmpty());
        }

        @Test
        void when_EntitiesAreFound_then_AListOfDcatDatasetIsReturned() {
            final String otherCollectionName = "other";
            DcatDatasetEntity secondEntity = mock();
            when(secondEntity.getCollectionName()).thenReturn(otherCollectionName);
            when(dcatDatasetEntityRepository.findAll()).thenReturn(List.of(entity, secondEntity));

            List<DcatDataset> result = repository.findAll();

            assertThat(result)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(new DcatDataset(DATASET_COLLECTION_NAME), new DcatDataset(otherCollectionName))
                    .first()
                    .matches(dcatDataset -> dcatDataset.getModel().isIsomorphicWith(readDcatDatasetModel()));
        }

    }

    private Model readDcatDatasetModel() {
        return RDFParser.source(MODEL_FILE_PATH).toModel();
    }

    private DcatDatasetEntity initializeEntity(Model dcatModel) {
        final EventStreamEntity eventStreamEntity = new EventStreamEntity(DATASET_COLLECTION_NAME, "", "", false, false);
        final DcatDatasetEntity datasetEntity = new DcatDatasetEntity(eventStreamEntity);
        datasetEntity.setModel(dcatModel);
        return datasetEntity;
    }
}
