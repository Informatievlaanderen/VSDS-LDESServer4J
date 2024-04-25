package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DcatDatasetMongoRepositoryTest {

	private static final String DATASET_ID = "id";
	private static final String MODEL_FILE_PATH = "dcat-dataset/dataset.ttl";
	private DcatDatasetEntity entity;
	private DcatDataset dataset;
	private DcatDatasetPostgresRespository mongoRepository;
	@Mock
	private DcatDatasetEntityRepository entityRepository;

	@BeforeEach
	void setUp() throws URISyntaxException, IOException {
		mongoRepository = new DcatDatasetPostgresRespository(entityRepository);
		Model model = readModelFromFile(MODEL_FILE_PATH);
		dataset = new DcatDataset(DATASET_ID, model);
		entity = new DcatDatasetEntity(DATASET_ID, readDataFromFile(MODEL_FILE_PATH));
	}

	@Test
	void when_DatasetPresent_Then_ReturnDataset() {
		when(entityRepository.findById(DATASET_ID)).thenReturn(Optional.of(entity));

		Optional<DcatDataset> actualDataset = mongoRepository.retrieveDataset(DATASET_ID);

		verify(entityRepository).findById(DATASET_ID);
		assertTrue(actualDataset.isPresent());
		assertEquals(dataset.getCollectionName(), actualDataset.get().getCollectionName());
		assertTrue(dataset.getModel().isIsomorphicWith(actualDataset.get().getModel()));
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

	@Nested
	class FindAll {
		@Test
		void when_NoEntitiesAreFound_then_AnEmptyListIsReturned() {
			when(entityRepository.findAll()).thenReturn(new ArrayList<>());

			List<DcatDataset> result = mongoRepository.findAll();

			assertTrue(result.isEmpty());
		}

		@Test
		void when_EntitiesAreFound_then_AListOfDcatDatasetIsReturned() throws Exception {
			String modelString = readDataFromFile(MODEL_FILE_PATH);
			DcatDatasetEntity dataset1 = new DcatDatasetEntity("col1", modelString);
			DcatDatasetEntity dataset2 = new DcatDatasetEntity("col2", modelString);
			when(entityRepository.findAll()).thenReturn(List.of(dataset1, dataset2));

			List<DcatDataset> result = mongoRepository.findAll();

			assertEquals(2, result.size());
			assertTrue(result.get(0).getModel().isIsomorphicWith(readModelFromFile(MODEL_FILE_PATH)));
			List<String> collections = result.stream().map(DcatDataset::getCollectionName).toList();
			assertTrue(collections.contains("col1"));
			assertTrue(collections.contains("col2"));
		}

	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}
}
