package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ViewSpecificationConverterTest {

    private static final String COLLECTION_NAME = "collection";
    private ViewSpecification view;

    @BeforeEach
    void setup() {
        view = new ViewSpecification();
    }

    @Test
    void when_ValidModel_Then_ReturnViewSpecification() throws URISyntaxException {
        Model viewModel = readModelFromFile("viewconverter/view_valid.ttl");
        ViewSpecification actualView = ViewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME);
        assertEquals(view, actualView);
    }

    @Test
    void when_MissingViewType_Then_ThrowException() throws URISyntaxException {
        Model viewModel = readModelFromFile("");
        assertThrows(ModelToViewConverterException.class, () -> ViewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME),
                "Could not convert model to ViewSpecification:\nMissing type: ");
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
                .toString();
        return RDFDataMgr.loadModel(uri);
    }

}