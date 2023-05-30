package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatDatasetValidator;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatDatasetServiceImplTest {
	private static final String DATASET_ID = "id";
	private static final String MODEL_FILE_PATH = "dcat-dataset/valid.ttl";
	private DcatDataset dataset;

	private DcatDatasetService datasetService;
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

			assertEquals("Resource of type: dcat-dataset with id: " + DATASET_ID + " could not be found.", e.getMessage());
			verify(repository).retrieveDataset(DATASET_ID);
			verifyNoMoreInteractions(repository);
		}
	}

	@Nested
	class RemoveDataset {
		@Test
		void removeDataset() {
			datasetService.deleteDataset(DATASET_ID);

			verify(repository).deleteDataset(DATASET_ID);
		}
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}
