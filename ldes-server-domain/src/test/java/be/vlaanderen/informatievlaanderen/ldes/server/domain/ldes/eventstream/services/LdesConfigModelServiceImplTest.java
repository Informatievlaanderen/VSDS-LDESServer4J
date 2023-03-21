package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
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

class LdesConfigModelServiceImplTest {
    LdesConfigModelService service;
    private LdesConfigRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(LdesConfigRepository.class);
        service = new LdesConfigModelServiceImpl(repository);
    }

    @Test
    void whenCollectionHasViews() throws URISyntaxException {
        final List<LdesConfigModel> models = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            Model view = readModelFromFile("ldes-" + i + ".ttl");
            models.add(new LdesConfigModel("collectionName" + i, view));
        }

        when(repository.retrieveAllLdesStreams()).thenReturn(models);
        assertEquals(service.retrieveAllEventStreams(), models);
    }

    @Test
    void whenCollectionHasViews_ThenReturnOneById() throws URISyntaxException {
        final String collectionName = "collectionName2";
        final Model view = readModelFromFile("ldes-2.ttl");
        final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, view);

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));
        assertEquals(ldesConfigModel, service.retrieveEventStream(collectionName));
    }

    @Test
    void whenCollectionNameDoesNotExist_CatchException() {
        final String collectionName = "collectionName3";

        String expectedMessage = "No ldes event stream exists with identifier: " + collectionName;

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.empty());
        assertThrows(MissingLdesConfigException.class, () -> service.retrieveEventStream(collectionName), expectedMessage);
    }

    @Test
    void whenCollectionNameExist_UpdateStream() throws URISyntaxException {
        final String collectionName = "collectionName1";

        final Model newModel = readModelFromFile("ldes-with-named-view.ttl");
        final LdesConfigModel newEventStreamModel = new LdesConfigModel(collectionName, newModel);

        when(repository.saveLdesStream(newEventStreamModel)).thenReturn(newEventStreamModel);
        service.updateEventStream(newEventStreamModel);
        verify(repository, times(1)).saveLdesStream(newEventStreamModel);
    }

    @Test
    void whenCollectionExists_RetrieveShape() throws URISyntaxException {
        final String collectionName = "collectionName2";
        final Model view = readModelFromFile("ldes-2.ttl");
        final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, view);
        final Model shape = ldesConfigModel.getModel().listStatements(null, createProperty(SHAPE), (Resource) null)
                .toList().stream()
                .findFirst().get().getModel();

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));
        assertTrue(service.retrieveShape(collectionName).isIsomorphicWith(shape));
    }

    @Test
    void whenCollectionExists_UpdateShape() throws URISyntaxException {
        final String collectionName = "collectionName1";

        final Model model = readModelFromFile("ldes-with-shape.ttl");
        final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

        final Model newShape = readModelFromFile("example-shape.ttl");
        final LdesConfigModel ldesStreamShape = new LdesConfigModel("shapeNew", newShape);

        final Model modelWitNewShape = readModelFromFile("ldes-with-named-view.ttl");

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));
        when(repository.saveLdesStream(ldesConfigModel)).thenReturn(ldesConfigModel);

        LdesConfigModel updatedLdesStreamShape = service.updateShape(collectionName, ldesStreamShape);

        assertTrue(ldesStreamShape.getModel().isIsomorphicWith(updatedLdesStreamShape.getModel()));
        assertTrue(ldesConfigModel.getModel().isIsomorphicWith(modelWitNewShape));
    }

    @Test
    void whenCollectionExists_RetrieveAllViews() throws URISyntaxException {
        final String collectionName = "collectionName2";
        final Model view = readModelFromFile("ldes-multiple-views.ttl");
        LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, view);

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));
        var views = service.retrieveViews(collectionName);
        assertEquals(3, service.retrieveViews(collectionName).size());
    }

    @Test
    void whenCollectionExistsWithMultipleViews_RetrieveSingleView() throws URISyntaxException {
        final String collectionName = "collectionName1";
        final Model model = readModelFromFile("ldes-named-view.ttl");
        LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

        final String viewName = "view1";

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));

        Model actualView = service.retrieveView(collectionName, viewName);
        Model expectedView = readModelFromFile("view.ttl");

        assertTrue(actualView.isIsomorphicWith(expectedView));
    }

    @Test
    void whenCollectionExistsWithoutViews_thenThrowException() throws URISyntaxException {
        final String collectionName = "collectionName1";
        final Model model = readModelFromFile("ldes-1.ttl");
        final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

        final String viewName = "nonExisting";

        final String expectedMessage = "No view exists with identifier: " + collectionName + "/" + viewName;

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));

        assertThrows(MissingLdesConfigException.class, () -> service.retrieveView(collectionName, viewName), expectedMessage);
    }

    @Test
    void whenCollectionHasNonExistingViewAndTriesToRetrieve_thenThrowException() throws URISyntaxException {
        final String collectionName = "collectionName1";
        final Model model = readModelFromFile("ldes-named-view.ttl");
        final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

        final String viewName = "nonExisting";

        final String expectedMessage = "No view exists with identifier: " + collectionName + "/" + viewName;

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));

        assertThrows(MissingLdesConfigException.class, () -> service.retrieveView(collectionName, viewName), expectedMessage);
    }

    @Test
    void whenCollectionHasView_thenDeleteOne() throws URISyntaxException {
        final String collectionName = "collectionName1";
        final Model model = readModelFromFile("ldes-named-view.ttl");
        final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

        final String viewName = "view1";

        final Model modelWithoutView = readModelFromFile("ldes-1.ttl");

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));
        when(repository.saveLdesStream(ldesConfigModel)).thenReturn(ldesConfigModel);

        service.deleteView(collectionName, viewName);

        assertTrue(modelWithoutView.isIsomorphicWith(model));
    }

    @Test
    void whenCollectionHasNonExistingViewAndTriesToDelete_thenThrowException() throws URISyntaxException {
        //TODO
        final String collectionName = "collectionName1";
        final Model model = readModelFromFile("ldes-named-view.ttl");
        final LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

        final String viewName = "nonExisting";

        final String expectedMessage = "No view exists with identifier: " + collectionName + "/" + viewName;

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));

        assertThrows(MissingLdesConfigException.class, () -> service.retrieveView(collectionName, viewName), expectedMessage);
    }

    @Test
    void whenCollectionExists_AddView() throws URISyntaxException {
        final String collectionName = "collectionName2";
        final Model model = readModelFromFile("ldes-multiple-views.ttl");
        LdesConfigModel ldesConfigModel = new LdesConfigModel(collectionName, model);

        final Model view = readModelFromFile("example-view.ttl");
        LdesConfigModel ldesStreamView = new LdesConfigModel("view1", view);

        when(repository.retrieveLdesStream(collectionName)).thenReturn(Optional.of(ldesConfigModel));
        when(repository.saveLdesStream(ldesConfigModel)).thenReturn(ldesConfigModel);

        assertEquals(service.addView(collectionName, ldesStreamView), ldesStreamView);
        assertEquals(4, service.retrieveViews(collectionName).size());
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource("eventstream/streams/" + fileName)).toURI()
                .toString();
        return RDFDataMgr.loadModel(uri);
    }

}