package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.config.LdesAdminConstants.SHAPE;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LdesStreamModelServiceImplTest {
	LdesStreamModelService ldesStreamModelService;
	private LdesStreamRepository repository;

	@BeforeEach
	void setUp() throws URISyntaxException {
		repository = mock(LdesStreamRepository.class);
		ldesStreamModelService = new LdesStreamModelServiceImpl(repository);
	}

	@Test
	void whenCollectionHasViews() throws URISyntaxException {
		final List<LdesStreamModel> models = new ArrayList<>();

		for (int i = 1; i < 6; i++) {
			Model view = readModelFromFile("example-view-" + i + ".ttl");
			models.add(new LdesStreamModel("collectionName" + i, view));
		}

		when(repository.retrieveAllLdesStreams()).thenReturn(models);
		assertEquals(ldesStreamModelService.retrieveAllEventStreams(), models);
	}

	@Test
	void whenCollectionHasViews_ThenReturnOneById() throws URISyntaxException {
		final String collectionName = "collectionName2";
		final Model view = readModelFromFile("example-view-2.ttl");
		final LdesStreamModel ldesStreamModel = new LdesStreamModel(collectionName, view);

		when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesStreamModel));
		assertEquals(ldesStreamModel, ldesStreamModelService.retrieveEventStream(collectionName));
	}

	@Test
	void whenCollectionNameDoesNotExist_CatchException() {
		final String collectionName = "collectionName3";
		when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.empty());
		assertThrows(MissingLdesStreamException.class, () -> ldesStreamModelService.retrieveEventStream(collectionName),
				() -> new MissingLdesStreamException(collectionName).getMessage());
	}

	@Test
	void whenCollectionNameExist_UpdateStream() throws URISyntaxException {
		final String collectionName = "collectionName1";

		final Model newModel = readModelFromFile("example-view-7.ttl");
		final LdesStreamModel newEventStreamModel = new LdesStreamModel(collectionName, newModel);

		when(repository.saveLdesStream(newEventStreamModel)).thenReturn(newEventStreamModel);
		ldesStreamModelService.updateEventStream(newEventStreamModel);
		verify(repository, times(1)).saveLdesStream(newEventStreamModel);
	}

	@Test
	void whenCollectionExists_RetrieveShape() throws URISyntaxException {
		final String collectionName = "collectionName2";
		final Model view = readModelFromFile("example-view-2.ttl");
		final LdesStreamModel ldesStreamModel = new LdesStreamModel(collectionName, view);
		final Model shape = ldesStreamModel.getModel().listStatements(null, createProperty(SHAPE), (Resource) null)
				.toList().stream()
				.findFirst().get().getModel();

		when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesStreamModel));
		assertTrue(ldesStreamModelService.retrieveShape(collectionName).isIsomorphicWith(shape));
	}

	@Test
	void whenCollectionExists_UpdateShape() throws URISyntaxException {
		final String collectionName = "collectionName1";

		final Model model = readModelFromFile("example-view-6.ttl");
		final LdesStreamModel ldesStreamModel = new LdesStreamModel(collectionName, model);

		final Model newShape = readModelFromFile("example-shape.ttl");
		final LdesStreamModel ldesStreamShape = new LdesStreamModel("shapeNew", newShape);

		final Model modelWitNewShape = readModelFromFile("example-view-7.ttl");

		when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesStreamModel));
		when(repository.saveLdesStream(ldesStreamModel)).thenReturn(ldesStreamModel);

		LdesStreamModel updatedLdesStreamShape = ldesStreamModelService.updateShape(collectionName, ldesStreamShape);

		assertTrue(ldesStreamShape.getModel().isIsomorphicWith(updatedLdesStreamShape.getModel()));
		assertTrue(ldesStreamModel.getModel().isIsomorphicWith(modelWitNewShape));
	}

	@Test
	void whenCollectionExists_RetrieveAllViews() throws URISyntaxException {
		final String collectionName = "collectionName2";
		final Model view = readModelFromFile("example-view-3.ttl");
		LdesStreamModel ldesStreamModel = new LdesStreamModel(collectionName, view);

		when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesStreamModel));
		var views = ldesStreamModelService.retrieveViews(collectionName);
		assertEquals(3, ldesStreamModelService.retrieveViews(collectionName).size());
	}

	@Test
	void whenCollectionExists_RetrieveSingleView() throws URISyntaxException {
		final String collectionName = "collectionName2";
		final Model model = readModelFromFile("example-view-4.ttl");
		LdesStreamModel ldesStreamModel = new LdesStreamModel(collectionName, model);

		final String viewName = "view1";

		when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesStreamModel));

		Model actualView = ldesStreamModelService.retrieveView(collectionName, viewName);
		Model expectedView = readModelFromFile("example-view-5.ttl");

		assertTrue(actualView.isIsomorphicWith(expectedView));
	}

	@Test
	void whenCollectionExists_AddView() throws URISyntaxException {
		final String collectionName = "collectionName2";
		final Model model = readModelFromFile("example-view-3.ttl");
		LdesStreamModel ldesStreamModel = new LdesStreamModel(collectionName, model);

		final Model view = readModelFromFile("example-view.ttl");
		LdesStreamModel ldesStreamView = new LdesStreamModel("view1", view);

		when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesStreamModel));
		when(repository.saveLdesStream(ldesStreamModel)).thenReturn(ldesStreamModel);

		assertEquals(ldesStreamModelService.addView(collectionName, ldesStreamView), ldesStreamView);
		assertEquals(4, ldesStreamModelService.retrieveViews(collectionName).size());
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource("eventstream/views/" + fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

}