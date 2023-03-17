package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository.LdesStreamRepository;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LdesStreamModelServiceImplTest {
    LdesStreamModelService ldesStreamModelService;

    private LdesStreamRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(LdesStreamRepository.class);
        ldesStreamModelService = new LdesStreamModelServiceImpl(repository);
    }

    @Test
    void retrieveViews() throws URISyntaxException {
        Model model = readModelFromFile("example-view.ttl");

        List<Statement> statements = model.listStatements(ResourceFactory.createResource("http://ldes.be/view1"), null, (Resource) null).toList();

        Model defaultModel = ModelFactory.createDefaultModel();
        defaultModel.add(statements);

        List<Statement> filteredStatements = defaultModel.listStatements().toList();

//        when(ldesStreamRepository.retrieveLdesStream(COLLECTION_NAME))
//                .thenReturn(Optional.of(new LdesStreamModel("id", )));

    }

    @Test
    void whenCollectionHasViews() throws URISyntaxException {
        final List<Model> models = new ArrayList<>();

        for(int i = 1; i < 4; i++) {
            Model view = readModelFromFile("example-view-" + i);
            models.add(view);
        }

//        when(repository.retrieveLdesStream())
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource("eventstream/views/" + fileName)).toURI().toString();
        return RDFDataMgr.loadModel(uri);

    }

}