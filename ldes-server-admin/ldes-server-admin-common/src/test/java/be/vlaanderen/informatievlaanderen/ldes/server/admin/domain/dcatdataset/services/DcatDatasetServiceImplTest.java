package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DcatDatasetServiceImplTest {
	private static final String DATASET_ID = "id";
	private static final String MODEL_FILE_PATH = "dcat/dataset/valid.ttl";
	private DcatDataset dataset;

	private DcatDatasetServiceImpl datasetService;
	@Mock
	private DcatDatasetRepository repository;

	@BeforeEach
	void setUp() throws URISyntaxException {
		datasetService = new DcatDatasetServiceImpl(repository);
		dataset = new DcatDataset(DATASET_ID, readModelFromFile(MODEL_FILE_PATH));
	}

	@Nested
	class saveDataset {
		@Test
		void when_DatasetNotPresent_then_SaveDataset() {
			Mockito.when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.empty());

			datasetService.saveDataset(dataset);

			Mockito.verify(repository).saveDataset(dataset);
		}

		@Test
		void when_DatasetPresent_then_ThrowException() {
			Mockito.when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.of(dataset));

			Exception e = Assertions.assertThrows(ExistingResourceException.class, () -> datasetService.saveDataset(dataset));

			Assertions.assertEquals("Resource of type: dcat-dataset with id: " + DATASET_ID + " already exists.", e.getMessage());
			Mockito.verify(repository).retrieveDataset(DATASET_ID);
			Mockito.verifyNoMoreInteractions(repository);
		}
	}

	@Nested
	class UpdateDataset {
		@Test
		void when_DatasetPresent_then_SaveDataset() {
			Mockito.when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.of(dataset));

			datasetService.updateDataset(dataset);

			Mockito.verify(repository).saveDataset(dataset);
		}

		@Test
		void when_DatasetNotPresent_then_ThrowException() {
			Mockito.when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.empty());

			Exception e = Assertions.assertThrows(MissingResourceException.class, () -> datasetService.updateDataset(dataset));

			Assertions.assertEquals("Resource of type: dcat-dataset with id: " + DATASET_ID + " could not be found.",
					e.getMessage());
			Mockito.verify(repository).retrieveDataset(DATASET_ID);
			Mockito.verifyNoMoreInteractions(repository);
		}
	}

	@Nested
	class RemoveDataset {
		@Test
		void removeDataset() {
			Mockito.when(repository.exitsByCollectionName(DATASET_ID)).thenReturn(true);

			datasetService.deleteDataset(DATASET_ID);

			Mockito.verify(repository).deleteDataset(DATASET_ID);
		}

		@Test
		void when_DatasetNotFound_Then_DatasetNotDeleted() {
			Mockito.when(repository.exitsByCollectionName(DATASET_ID)).thenReturn(false);

			datasetService.deleteDataset(DATASET_ID);

			Mockito.verify(repository, Mockito.never()).deleteDataset(DATASET_ID);
		}
	}

	@Test
	void should_CallDcatServiceToGetDcat_when_GetComposedDcatIsCalled() {
		List<DcatDataset> datasets = new ArrayList<>();
		datasets.add(dataset);
		Mockito.when(repository.findAll()).thenReturn(datasets);

		List<DcatDataset> result = repository.findAll();

		Assertions.assertEquals(datasets, result);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}
