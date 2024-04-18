package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat.DcatDatasetValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatDatasetServiceImplTest {
	private static final String DATASET_ID = "id";
	private static final String MODEL_FILE_PATH = "dcat/dataset/valid.ttl";
	private DcatDataset dataset;

	private DcatDatasetServiceImpl datasetService;
	@Mock
	private DcatDatasetRepository repository;
	@Mock
	private DcatDatasetValidator validator;

	@BeforeEach
	void setUp() throws URISyntaxException {
		datasetService = new DcatDatasetServiceImpl(repository);
		dataset = new DcatDataset(DATASET_ID, readModelFromFile(MODEL_FILE_PATH));
	}

	@Nested
	class saveDataset {
		@Test
		void when_DatasetNotPresent_then_SaveDataset() {
			when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.empty());

			datasetService.saveDataset(dataset);

			verify(repository).saveDataset(dataset);
		}

		@Test
		void when_DatasetPresent_then_ThrowException() {
			when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.of(dataset));

			Exception e = assertThrows(ExistingResourceException.class, () -> datasetService.saveDataset(dataset));

			assertEquals("Resource of type: dcat-dataset with id: " + DATASET_ID + " already exists.", e.getMessage());
			verify(repository).retrieveDataset(DATASET_ID);
			verifyNoMoreInteractions(repository);
		}
	}

	@Nested
	class UpdateDataset {
		@Test
		void when_DatasetPresent_then_SaveDataset() {
			when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.of(dataset));

			datasetService.updateDataset(dataset);

			verify(repository).saveDataset(dataset);
		}

		@Test
		void when_DatasetNotPresent_then_ThrowException() {
			when(repository.retrieveDataset(DATASET_ID)).thenReturn(Optional.empty());

			Exception e = assertThrows(MissingResourceException.class, () -> datasetService.updateDataset(dataset));

			assertEquals("Resource of type: dcat-dataset with id: " + DATASET_ID + " could not be found.",
					e.getMessage());
			verify(repository).retrieveDataset(DATASET_ID);
			verifyNoMoreInteractions(repository);
		}
	}

	@Nested
	class RemoveDataset {
		@Test
		void removeDataset() {
			when(datasetService.retrieveDataset(DATASET_ID)).thenReturn(Optional.of(dataset));

			datasetService.deleteDataset(DATASET_ID);

			verify(repository).deleteDataset(DATASET_ID);
		}

		@Test
		void when_DatasetNotFound_Then_DatasetNotDeleted() {
			when(datasetService.retrieveDataset(DATASET_ID)).thenReturn(Optional.empty());

			datasetService.deleteDataset(DATASET_ID);

			verify(repository, never()).deleteDataset(DATASET_ID);
		}
	}

	@Test
	void should_CallDcatServiceToGetDcat_when_GetComposedDcatIsCalled() {
		List<DcatDataset> datasets = new ArrayList<>();
		datasets.add(dataset);
		when(repository.findAll()).thenReturn(datasets);

		List<DcatDataset> result = repository.findAll();

		assertEquals(datasets, result);
	}

	@Test
	void should_DeleteDataset_when_EventStreamDeletedEventIsReceived() {
		String collectionName = "collectionName";
		when(repository.retrieveDataset(collectionName)).thenReturn(Optional.of(new DcatDataset(collectionName)));

		datasetService.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(collectionName));

		verify(repository).deleteDataset(collectionName);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}
