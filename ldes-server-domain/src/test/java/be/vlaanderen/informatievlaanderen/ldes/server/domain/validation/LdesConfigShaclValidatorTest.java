package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LdesConfigShaclValidatorTest {
    private LdesConfigShaclValidator validator;

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
//        validator = new LdesConfigShaclValidator(readShaclShape("streamShaclShape.ttl"));
        validator = new LdesConfigShaclValidator("streamShaclShape.ttl");
    }

    private String readShaclShape(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URI uri = null;
        uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

        Stream<String> shape = Files.lines(Paths.get(uri));
        return shape.collect(Collectors.joining("\n"));
    }

    @Test
    void when_SupportedClassProvided_thenReturnTrue() {
        assertTrue(validator.supports(LdesConfigModel.class));
    }

    @Test
    void when_UnsupportedClassProvided_thenReturnFalse() {
        assertFalse(validator.supports(EventStream.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void when_ValidateProvidedValidData_thenReturnValid() throws URISyntaxException {
        final Model modelWithShape = readModelFromFile("ldes-with-shape.ttl");
        final LdesConfigModel ldesConfigModelWithShape = new LdesConfigModel("collectionName", modelWithShape);

        final Model modelWithoutShape = readModelFromFile("ldes-2.ttl");
        final LdesConfigModel ldesConfigModelWithoutShape = new LdesConfigModel("collectionName", modelWithoutShape);

        assertDoesNotThrow(() -> validator.validateShape(ldesConfigModelWithShape.getModel()));
        assertDoesNotThrow(() -> validator.validateShape(ldesConfigModelWithoutShape.getModel()));
    }

    @Test
    @Disabled("Disabled until the right shacl shapes are provided")
    void when_ValidateProvidedInvalidData_thenReturnInvalid() throws URISyntaxException {
        final Model model = readModelFromFile("ldes-without-version-of-path.ttl");
        assertThrows(LdesShaclValidationException.class, () -> validator.validateShape(model));
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource("eventstream/streams/" + fileName)).toURI()
                .toString();
        return RDFDataMgr.loadModel(uri);
    }
}
