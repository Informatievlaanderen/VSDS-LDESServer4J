package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

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
    void when_IncorrectMemberReceived_Then_ValidationThrowsException(Model model, String collectionName, List<String> expectedMessages) {
        String actualMessage = assertThrows(ShaclValidationException.class, () -> validator.validate(model, collectionName)).getMessage();
        expectedMessages.forEach(expectedMessage -> assertTrue(actualMessage.contains(expectedMessage)));
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
                            List.of("Member must have exactly 0 statements with timestamp path: " + TIMESTAMP_PATH,
                                    "Member must have exactly 0 statements with versionOf path: " + VERSIONOF_PATH)),
                    Arguments.of(readModelFromFile("example-ldes-member-multiple-version-ofs.nq"), STATE,
                            List.of("Member must have exactly 0 statements with timestamp path: " + TIMESTAMP_PATH,
                                    "Member must have exactly 0 statements with versionOf path: " + VERSIONOF_PATH)),
                    Arguments.of(readModelFromFile("example-ldes-member-without-root-timestamp.nq"), VERSION,
                            List.of("Member must have exactly 1 statement with timestamp path: " + TIMESTAMP_PATH)),
                    Arguments.of(readModelFromFile("example-ldes-member-multiple-version-ofs.nq"), VERSION,
                            List.of("Member must have exactly 1 statement with versionOf path: " + VERSIONOF_PATH)),
                    Arguments.of(readModelFromFile("example-ldes-member-without-version-of.nq"), VERSION,
                            List.of("Member must have exactly 1 statement with versionOf path: " + VERSIONOF_PATH)),
                    Arguments.of(readModelFromFile("example-ldes-member-wrong-type-version-of.nq"), VERSION,
                            List.of("Object of statement with predicate: " + VERSIONOF_PATH + " should be a resource")),
                    Arguments.of(readModelFromFile("example-ldes-member-wrong-type-timestamp.nq"), VERSION,
                            List.of("Object of statement with predicate: " + TIMESTAMP_PATH + " should be a literal of type " + XSDDatatype.XSDdateTime.getURI())));
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