package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.pathsingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.IngestValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathsIngestValidatorTest {
    private final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
    private final String VERSIONOF_PATH = "http://purl.org/dc/terms/isVersionOf";
    private PathsIngestValidator validator;

    @BeforeEach
    void setup() {
        validator = new PathsIngestValidator();
        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(
                new EventStream("state", TIMESTAMP_PATH, VERSIONOF_PATH, true)));
        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(
                new EventStream("version",TIMESTAMP_PATH, VERSIONOF_PATH, false)));
    }

    @ParameterizedTest
    @ArgumentsSource(IncorrectMemberArgumentsProvider.class)
    void when_IncorrectMemberReceived_Then_ValidationThrowsException(Model model, String collectionName) {
        assertThrows(IngestValidationException.class, () -> validator.validate(model, collectionName));
    }
    @ParameterizedTest
    @ArgumentsSource(CorrectMemberArgumentsProvider.class)
    void when_CorrectMemberReceived_Then_ValidationDoesNotThrowException(Model model, String collectionName) {
        assertDoesNotThrow(() -> validator.validate(model, collectionName));
    }


    static class IncorrectMemberArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of(readModelFromFile("example-ldes-member.nq"), "state"),
                    Arguments.of(readModelFromFile("example-ldes-member-without-root-timestamp.nq"), "version"),
                    Arguments.of(readModelFromFile("example-ldes-member-multiple-version-ofs.nq"), "version"),
                    Arguments.of(readModelFromFile("example-ldes-member-without-version-of.nq"), "version"));
        }
    }
    static class CorrectMemberArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of(readModelFromFileCorrectMember("example-ldes-member-state.nq"), "state"),
                    Arguments.of(readModelFromFileCorrectMember("example-ldes-member.nq"), "version"));
        }
    }

    private static Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = IncorrectMemberArgumentsProvider.class.getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
                .toString();
        return RDFDataMgr.loadModel(uri);
    }

    private static Model readModelFromFileCorrectMember(String fileName) throws URISyntaxException {
        ClassLoader classLoader = CorrectMemberArgumentsProvider.class.getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
                .toString();
        return RDFDataMgr.loadModel(uri);
    }
}