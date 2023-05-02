package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.exceptions.InvalidConfigOperationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdesConfigModelServiceImplTest {
	private static final String COLLECTION_NAME_1 = "collectionName1";
	private LdesConfigModelService service;
	@Mock
	private LdesConfigRepository repository;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Captor
	private ArgumentCaptor<ShaclChangedEvent> shaclChangedEventCaptor;

	@BeforeEach
	void setUp() {
		service = new LdesConfigModelServiceImpl(repository, eventPublisher);
	}

	@Nested
	class EventStreams {
		@Test
		void whenCollectionExists_thenDelete() throws URISyntaxException {
			final Model model = readModelFromFile("ldes-empty.ttl");
			final LdesConfigModel ldesConfigModel = new LdesConfigModel(COLLECTION_NAME_1, model);

			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel))
					.thenReturn(Optional.empty());

			service.deleteConfigModel(COLLECTION_NAME_1);

			verify(repository, times(1)).retrieveConfigModel(COLLECTION_NAME_1);
			verify(repository, times(1)).deleteConfigModel(COLLECTION_NAME_1);
			verify(repository, times(1)).retrieveConfigModel(COLLECTION_NAME_1);
			assertThrows(MissingLdesConfigException.class, () -> service.retrieveConfigModel(COLLECTION_NAME_1));
		}

		@Test
		void whenNonExistingCollectionDeleted_thenThrowError() {
			final String collectionName = "non-existing";

			when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.empty());

			final String expectedMessage = "No ldes event stream exists with identifier: " + collectionName;
			assertThrows(MissingLdesConfigException.class, () -> service.deleteConfigModel(collectionName),
					expectedMessage);
			verify(repository, times(1)).retrieveConfigModel(collectionName);
			verify(repository, times(0)).deleteConfigModel(collectionName);
		}

		@Test
		void whenStreamsRetrieved_thenReturnAll() throws URISyntaxException {
			Map<String, String> ldesFiles = Map.of("ldes-multiple-views.ttl", "collectionName2",
					"ldes-empty.ttl", "collectionName1",
					"ldes-with-named-view.ttl", "collectionName1",
					"ldes-with-shape.ttl", "collectionName1");
			final List<LdesConfigModel> ldesConfigModels = new ArrayList<>();

			for (Map.Entry<String, String> entry : ldesFiles.entrySet()) {
				Model model = readModelFromFile(entry.getKey());
				ldesConfigModels.add(new LdesConfigModel(entry.getValue(), model));
			}

			when(repository.retrieveAllConfigModels()).thenReturn(ldesConfigModels);
			assertEquals(service.retrieveAllConfigModels(), ldesConfigModels);
		}

		@Test
		void whenCollectionNameDoesNotExist_CatchException() {
			final String collectionName = "collectionName3";

			String expectedMessage = "No ldes event stream exists with identifier: " + collectionName;

			when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.empty());
			assertThrows(MissingLdesConfigException.class, () -> service.retrieveConfigModel(collectionName),
					expectedMessage);
		}

		@Test
		void whenCollectionNameExist_UpdateStream() throws URISyntaxException {
			final Model newModel = readModelFromFile("ldes-with-named-view.ttl");
			final LdesConfigModel newEventStreamModel = new LdesConfigModel(COLLECTION_NAME_1, newModel);

			final Model expectedShape = readModelFromFile("example-shape.ttl");
			final ShaclChangedEvent expectedShaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME_1, expectedShape);

			when(repository.saveConfigModel(newEventStreamModel)).thenReturn(newEventStreamModel);
			service.updateConfigModel(newEventStreamModel);
			verify(repository, times(1)).saveConfigModel(newEventStreamModel);
			verify(eventPublisher).publishEvent(shaclChangedEventCaptor.capture());
			ShaclChangedEvent shaclChangedEvent = shaclChangedEventCaptor.getValue();
			assertEquals(expectedShaclChangedEvent, shaclChangedEvent);
		}
	}

	@Nested
	class Views {
		private LdesConfigModel ldesConfigModel;

		@BeforeEach
		void setUp() throws URISyntaxException {
			final Model model = readModelFromFile("ldes-with-named-view.ttl");
			ldesConfigModel = new LdesConfigModel(COLLECTION_NAME_1, model);
		}

		@Test
		void whenCollectionHasViews_ThenReturnOneById() {
			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel));
			assertEquals(ldesConfigModel, service.retrieveConfigModel(COLLECTION_NAME_1));
		}

		@Test
		void whenCollectionExists_RetrieveAllViews() throws URISyntaxException {
			final String collectionName = "collectionName2";
			final Model view = readModelFromFile("ldes-multiple-views.ttl");
			LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, view);

			when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));
			assertEquals(3, service.retrieveViews(collectionName).size());
		}

		@Test
		void whenCollectionExistsWithMultipleViews_RetrieveSingleView() throws URISyntaxException {
			final String viewName = "view1";

			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel));

			Model actualView = service.retrieveView(COLLECTION_NAME_1, viewName).getModel();
			Model expectedView = readModelFromFile("view.ttl");

			assertTrue(actualView.isIsomorphicWith(expectedView));
		}

		@Test
		void whenCollectionExistsWithoutViews_thenThrowException() throws URISyntaxException {
			final Model model = readModelFromFile("ldes-empty.ttl");
			final LdesConfigModel ldesConfigModel = new LdesConfigModel(COLLECTION_NAME_1, model);
			final String viewName = "nonExisting";
			final String expectedMessage = "No view exists with identifier: " + COLLECTION_NAME_1 + "/" + viewName;

			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel));

			assertThrows(MissingLdesConfigException.class, () -> service.retrieveView(COLLECTION_NAME_1, viewName),
					expectedMessage);
		}

		@Test
		void whenCollectionHasNonExistingViewAndTriesToRetrieve_thenThrowException() {
			final String viewName = "nonExisting";
			final String expectedMessage = "No view exists with identifier: " + COLLECTION_NAME_1 + "/" + viewName;

			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel));

			assertThrows(MissingLdesConfigException.class, () -> service.retrieveView(COLLECTION_NAME_1, viewName),
					expectedMessage);
		}

		@Test
		void whenCollectionHasView_thenDeleteOne() throws URISyntaxException {
			final String viewName = "view1";
			final Model modelWithoutView = readModelFromFile("ldes-empty.ttl");

			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel));
			when(repository.saveConfigModel(ldesConfigModel)).thenReturn(ldesConfigModel);

			service.deleteView(COLLECTION_NAME_1, viewName);

			assertTrue(modelWithoutView.isIsomorphicWith(ldesConfigModel.getModel()));
		}

		@Test
		void whenCollectionHasNonExistingViewAndTriesToDelete_thenThrowException() {
			final String viewName = "nonExisting";
			final String expectedMessage = "No view exists with identifier: " + COLLECTION_NAME_1 + "/" + viewName;

			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel));

			assertThrows(MissingLdesConfigException.class, () -> service.deleteView(COLLECTION_NAME_1, viewName),
					expectedMessage);
		}

		@Test
		void whenCollectionExists_AddView() throws URISyntaxException {
			final String collectionName = "collectionName2";
			final String viewName = "view1";
			final Model model = readModelFromFile("ldes-multiple-views.ttl");
			LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

			final Model view = readModelFromFile("view.ttl");

			when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));
			when(repository.saveConfigModel(ldesConfigModel)).thenReturn(ldesConfigModel);

			assertEquals(3, service.retrieveViews(collectionName).size());

			LdesConfigModel ldesConfigView = new LdesConfigModel(viewName, view);
			Model addedView = service.addView(collectionName, ldesConfigView).getModel();

			assertTrue(addedView.isIsomorphicWith(view));

			assertEquals(4, service.retrieveViews(collectionName).size());
			Model retrievedView = service.retrieveView(collectionName, viewName).getModel();
			assertTrue(retrievedView.isIsomorphicWith(view));
		}

		@Test
		void whenCollectionExistsAndTriesToAddExistingView_thenThrowException() throws URISyntaxException {
			final String viewName = "view1";

			final Model newView = readModelFromFile("updated-view.ttl");
			final LdesConfigModel newLdesConfigView = new LdesConfigModel("view1", newView);

			when(repository.retrieveConfigModel(COLLECTION_NAME_1)).thenReturn(Optional.of(ldesConfigModel));

			assertThrows(InvalidConfigOperationException.class,
					() -> service.addView(COLLECTION_NAME_1, newLdesConfigView),
					"Unable to complete operation.\nCause: View with id: " + viewName + " already exists.");
			verify(repository, never()).saveConfigModel(any());
		}

	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource("eventstream/streams/" + fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

}
