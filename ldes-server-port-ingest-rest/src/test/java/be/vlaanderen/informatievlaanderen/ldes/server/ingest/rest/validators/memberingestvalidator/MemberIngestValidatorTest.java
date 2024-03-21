package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

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
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.XML_DATETIME;
import static org.junit.jupiter.api.Assertions.*;

class MemberIngestValidatorTest {
    private static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
    private static final String VERSIONOF_PATH = "http://purl.org/dc/terms/isVersionOf";
    private static final String STATE = "state";
    private static final String VERSION = "version";
    private MemberIngestValidator validator;

    @BeforeEach
    void setup() {
        validator = new MemberIngestValidator();
        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(
                new EventStream(STATE, TIMESTAMP_PATH, VERSIONOF_PATH, true)));
        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(
                new EventStream(VERSION,TIMESTAMP_PATH, VERSIONOF_PATH, false)));
    }

    @ParameterizedTest
    @ArgumentsSource(IncorrectMemberArgumentsProvider.class)
    void when_IncorrectMemberReceived_Then_ValidationThrowsException(Model model, String collectionName, String expectedMessage) {
        String actualMessage = assertThrows(IngestValidationException.class, () -> validator.validate(model, collectionName), expectedMessage).getMessage();
        assertEquals(expectedMessage, actualMessage);
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
                    Arguments.of(readModelFromFile("example-ldes-member.nq"), STATE,
                            "Member ingested on collection " + STATE + " should not contain the timestamp path: " + TIMESTAMP_PATH +
                            " and the version of path: " + VERSIONOF_PATH),
                    Arguments.of(readModelFromFile("example-ldes-member-multiple-version-ofs.nq"), STATE,
                            "Member ingested on collection " + STATE + " should not contain the timestamp path: " + TIMESTAMP_PATH +
                                    " and the version of path: " + VERSIONOF_PATH),
                    Arguments.of(readModelFromFile("example-ldes-member-without-root-timestamp.nq"), VERSION,
                            "Member ingested on collection " + VERSION + " should contain the timestamp path: " + TIMESTAMP_PATH +
                                    " exactly once."),
                    Arguments.of(readModelFromFile("example-ldes-member-multiple-version-ofs.nq"), VERSION,
                            "Member ingested on collection " + VERSION + " should contain the version of path: " + VERSIONOF_PATH +
                                    " exactly once."),
                    Arguments.of(readModelFromFile("example-ldes-member-without-version-of.nq"), VERSION,
                            "Member ingested on collection " + VERSION + " should contain the version of path: " + VERSIONOF_PATH +
                                    " exactly once."),
                    Arguments.of(readModelFromFile("example-ldes-member-wrong-type-version-of.nq"), VERSION,
                            "Object of statement with property: " + VERSIONOF_PATH + " should be a resource."),
                    Arguments.of(readModelFromFile("example-ldes-member-wrong-type-timestamp.nq"), VERSION,
                            "Object of statement with property: " + TIMESTAMP_PATH + " should be a literal of type " + XML_DATETIME.getURI()));
        }
    }

    static class CorrectMemberArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of(readModelFromFile("example-ldes-member-state.nq"), STATE),
                    Arguments.of(readModelFromFile("example-ldes-member.nq"), VERSION));
        }
    }

    private static Model readModelFromFile(String fileName) throws URISyntaxException {
        return RDFDataMgr.loadModel(fileName);
    }
}