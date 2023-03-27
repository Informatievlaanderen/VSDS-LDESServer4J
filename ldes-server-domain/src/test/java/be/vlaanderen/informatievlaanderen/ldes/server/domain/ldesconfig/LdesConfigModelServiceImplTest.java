package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LdesConfigModelServiceImplTest {
	LdesConfigModelService service;
	private LdesConfigRepository repository;

	@BeforeEach
	void setUp() {
		repository = mock(LdesConfigRepository.class);
		service = new LdesConfigModelServiceImpl(repository);
	}

	@Test
	void whenCollectionExists_thenDelete() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final Model model = readModelFromFile("ldes-empty.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel))
				.thenReturn(Optional.empty());

		service.deleteConfigModel(collectionName);

		verify(repository, times(1)).retrieveConfigModel(collectionName);
		verify(repository, times(1)).deleteConfigModel(collectionName);
		verify(repository, times(1)).retrieveConfigModel(collectionName);
		assertThrows(MissingLdesConfigException.class, () -> service.retrieveConfigModel(collectionName));
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
	void whenCollectionHasViews_ThenReturnOneById() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final Model view = readModelFromFile("ldes-with-named-view.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, view);

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));
		assertEquals(ldesConfigModel, service.retrieveConfigModel(collectionName));
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
		final String collectionName = "collectionName1";

		final Model newModel = readModelFromFile("ldes-with-named-view.ttl");
		final LdesConfigModel newEventStreamModel = new LdesConfigModel(collectionName, newModel);

		when(repository.saveConfigModel(newEventStreamModel)).thenReturn(newEventStreamModel);
		service.updateConfigModel(newEventStreamModel);
		verify(repository, times(1)).saveConfigModel(newEventStreamModel);
	}

	@Test
	void whenCollectionExists_RetrieveShape() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final Model model = readModelFromFile("ldes-with-named-view.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);
		final Model shape = readModelFromFile("example-shape.ttl");

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));
		assertTrue(service.retrieveShape(collectionName).getModel().isIsomorphicWith(shape));
	}

	@Test
	void whenCollectionExists_UpdateShape() throws URISyntaxException {
		final String collectionName = "collectionName1";

		final Model model = readModelFromFile("ldes-with-shape.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		final Model newShape = readModelFromFile("example-shape.ttl");
		final LdesConfigModel ldesConfigShape = new LdesConfigModel("shape", newShape);

		final Model modelWitNewShape = readModelFromFile("ldes-with-named-view.ttl");

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));
		when(repository.saveConfigModel(ldesConfigModel)).thenReturn(ldesConfigModel);

		LdesConfigModel updatedLdesConfigShape = service.updateShape(collectionName, ldesConfigShape);

		assertTrue(ldesConfigShape.getModel().isIsomorphicWith(updatedLdesConfigShape.getModel()));
		assertTrue(ldesConfigModel.getModel().isIsomorphicWith(modelWitNewShape));
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
		final String collectionName = "collectionName1";
		final Model model = readModelFromFile("ldes-with-named-view.ttl");
		LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		final String viewName = "view1";

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));

		Model actualView = service.retrieveView(collectionName, viewName).getModel();
		Model expectedView = readModelFromFile("view.ttl");

		assertTrue(actualView.isIsomorphicWith(expectedView));
	}

	@Test
	void whenCollectionExistsWithoutViews_thenThrowException() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final Model model = readModelFromFile("ldes-empty.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		final String viewName = "nonExisting";

		final String expectedMessage = "No view exists with identifier: " + collectionName + "/" + viewName;

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));

		assertThrows(MissingLdesConfigException.class, () -> service.retrieveView(collectionName, viewName),
				expectedMessage);
	}

	@Test
	void whenCollectionHasNonExistingViewAndTriesToRetrieve_thenThrowException() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final Model model = readModelFromFile("ldes-with-named-view.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		final String viewName = "nonExisting";

		final String expectedMessage = "No view exists with identifier: " + collectionName + "/" + viewName;

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));

		assertThrows(MissingLdesConfigException.class, () -> service.retrieveView(collectionName, viewName),
				expectedMessage);
	}

	@Test
	void whenCollectionHasView_thenDeleteOne() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final Model model = readModelFromFile("ldes-with-named-view.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		final String viewName = "view1";

		final Model modelWithoutView = readModelFromFile("ldes-empty.ttl");

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));
		when(repository.saveConfigModel(ldesConfigModel)).thenReturn(ldesConfigModel);

		service.deleteView(collectionName, viewName);

		assertTrue(modelWithoutView.isIsomorphicWith(model));
	}

	@Test
	void whenCollectionHasNonExistingViewAndTriesToDelete_thenThrowException() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final Model model = readModelFromFile("ldes-with-named-view.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		final String viewName = "nonExisting";

		final String expectedMessage = "No view exists with identifier: " + collectionName + "/" + viewName;

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));

		assertThrows(MissingLdesConfigException.class, () -> service.deleteView(collectionName, viewName),
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
	void whenCollectionExistsAndTriesToAddExistingView_thenUpdateView() throws URISyntaxException {
		final String collectionName = "collectionName1";
		final String viewName = "view1";

		final Model model = readModelFromFile("ldes-with-named-view.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

		final Model newView = readModelFromFile("updated-view.ttl");
		final LdesConfigModel newLdesConfigView = new LdesConfigModel("view1", newView);

		when(repository.retrieveConfigModel(collectionName)).thenReturn(Optional.of(ldesConfigModel));
		when(repository.saveConfigModel(ldesConfigModel)).thenReturn(ldesConfigModel);

		assertEquals(1, service.retrieveViews(collectionName).size());

		final Model oldView = service.retrieveView(collectionName, viewName).getModel();
		assertFalse(oldView.isIsomorphicWith(newView), "make sure the views are not equal/isomorphic");
		assertEquals(1, service.retrieveViews(collectionName).size(),
				"Make sure there is only one view in the collection");

		service.addView(collectionName, newLdesConfigView);

		verify(repository, times(1)).saveConfigModel(ldesConfigModel);
		assertEquals(1, service.retrieveViews(collectionName).size(),
				"make sure the view is updated and not added as second element");

		final Model updatedView = service.retrieveView(collectionName, viewName).getModel();
		assertTrue(updatedView.isIsomorphicWith(newView));
		assertFalse(updatedView.isIsomorphicWith(oldView));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource("eventstream/streams/" + fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

}
