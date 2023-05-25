package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.validation.Errors;
import org.springframework.web.bind.EscapedErrors;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DcatDatasetValidatorTest {
    private static final String DATASET_ID = "id";
    private DcatDatasetValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DcatDatasetValidator();
    }

    @Test
    void when_ValidModel_Then_Pass() throws URISyntaxException {
        DcatDataset dataset = new DcatDataset(DATASET_ID, readModelFromFile("dcat-dataset/valid.ttl"));
        assertDoesNotThrow(() -> validator.validate(dataset.model()));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidArgumentsProvider.class)
    void when_InvalidModel_Then_throwException(String filePath, String expectedMessage) throws URISyntaxException {
        DcatDataset dataset = new DcatDataset(DATASET_ID, readModelFromFile(filePath));
        Model datasetModel = dataset.model();
        Exception exception = assertThrows(RuntimeException.class, () -> validator.validate(datasetModel));
        assertEquals(expectedMessage, exception.getMessage());
    }


    static class InvalidArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("dcat-dataset/not-anon.ttl", ""),
                    Arguments.of("dcat-dataset/illegal-property.ttl", ""),
                    Arguments.of("dcat-dataset/illegal-relation.ttl", ""));
        }
    }
    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
                .toString();
        return RDFDataMgr.loadModel(uri);
    }


}