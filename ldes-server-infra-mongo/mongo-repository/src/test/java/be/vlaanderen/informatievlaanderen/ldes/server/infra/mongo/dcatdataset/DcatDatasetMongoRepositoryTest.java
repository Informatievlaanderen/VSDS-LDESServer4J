package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.repository.DcatDatasetEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DcatDatasetMongoRepositoryTest {

	private static final String DATASET_ID = "id";
	private static final String MODEL_FILE_PATH = "dcat-dataset/dataset.ttl";
	private DcatDatasetEntity entity;
	private DcatDataset dataset;
	private DcatDatasetMongoRepository mongoRepository;
	@Mock
	private DcatDatasetEntityRepository entityRepository;

	@BeforeEach
	void setUp() throws URISyntaxException {
		mongoRepository = new DcatDatasetMongoRepository(entityRepository);
		Model model = readModelFromFile(MODEL_FILE_PATH);
		dataset = new DcatDataset(DATASET_ID, model);
		entity = new DcatDatasetEntity(DATASET_ID, model);
	}

	@Test
    void when_DatasetPresent_Then_ReturnDataset() {
        when(entityRepository.findById(DATASET_ID)).thenReturn(Optional.of(entity));

        Optional<DcatDataset> actualDataset = mongoRepository.retrieveDataset(DATASET_ID);

        verify(entityRepository).findById(DATASET_ID);
        assertTrue(actualDataset.isPresent());
        assertEquals(dataset, actualDataset.get());
    }

	@Test
	void when_DatasetNotPresent_Then_ReturnEmpty() {
		final String otherId = "other";
		when(entityRepository.findById(otherId)).thenReturn(Optional.empty());

		Optional<DcatDataset> actualDataset = mongoRepository.retrieveDataset(otherId);

		verify(entityRepository).findById(otherId);
		assertTrue(actualDataset.isEmpty());
	}

	@Test
	void when_DatasetAdded() {
		mongoRepository.saveDataset(dataset);

		verify(entityRepository).save(any(DcatDatasetEntity.class));
	}

	@Test
	void when_DatasetPresent_Then_DatasetRemoved() {
		mongoRepository.deleteDataset(DATASET_ID);

		verify(entityRepository).deleteById(DATASET_ID);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}