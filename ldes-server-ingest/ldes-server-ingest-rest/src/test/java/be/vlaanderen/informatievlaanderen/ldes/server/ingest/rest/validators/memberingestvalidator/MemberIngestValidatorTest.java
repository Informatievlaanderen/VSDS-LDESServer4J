package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.memberingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.VersionCreationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator.BlankNodesValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator.PathsValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.memberingestvalidator.MemberIngestValidator;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.assertj.core.api.Condition;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberIngestValidatorTest {
    private static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
    private static final String VERSIONOF_PATH = "http://purl.org/dc/terms/isVersionOf";
    private static final String STATE = "state";
    private static final String VERSION = "version";
    private MemberIngestValidator validator;

    @BeforeEach
    void setup() {
        validator = new MemberIngestValidator(List.of(new BlankNodesValidator(), new PathsValidator()));
        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(
                new EventStream(STATE, TIMESTAMP_PATH, VERSIONOF_PATH, VersionCreationProperties.enabledWithDefault())));
        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(
                new EventStream(VERSION,TIMESTAMP_PATH, VERSIONOF_PATH, VersionCreationProperties.disabled())));
    }

    @ParameterizedTest(name = "Receiving incorrect member {0}")
    @ArgumentsSource(IncorrectMemberArgumentsProvider.class)
    void when_IncorrectMemberReceived_Then_ValidationThrowsException(String modelName, String collectionName, List<String> expectedMessages) {
        Model model = RDFDataMgr.loadModel(modelName);
        String actualMessage = assertThrows(ShaclValidationException.class, () -> validator.validate(model, collectionName)).getMessage();
        assertThat(actualMessage).has(errorMessages(expectedMessages));
    }

    @ParameterizedTest
    @ArgumentsSource(CorrectMemberArgumentsProvider.class)
    void when_CorrectMemberReceived_Then_ValidationDoesNotThrowException(Model model, String collectionName) {
        assertDoesNotThrow(() -> validator.validate(model, collectionName));
    }

    @ParameterizedTest
    @ArgumentsSource(CorrectMemberArgumentsProvider.class)
    void when_EventStreamClosedEvent_validationThrowsException(Model model, String collectionName) {
        EventStreamClosedEvent eventStreamClosedEvent = new EventStreamClosedEvent(collectionName);
        validator.handleEventStreamClosedEvent(eventStreamClosedEvent);

        assertThatThrownBy(() -> validator.validate(model, collectionName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("collection %s is closed".formatted(collectionName));
    }

    @Test
    void when_EventStreamInit_Then_EventstreamAddedToMap() {
        EventStream stream = new EventStream("new", TIMESTAMP_PATH, VERSIONOF_PATH, null);

        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(stream));

        assertThat(validator)
                .extracting("eventstreams", InstanceOfAssertFactories.collection(EventStream.class))
                .contains(stream);
    }

    @Test
    void when_ClosedEventStreamInit_Then_EventstreamAddedToMap() {
        EventStream stream = new EventStream("closed", TIMESTAMP_PATH, VERSIONOF_PATH, VersionCreationProperties.disabled(), true, null);

        validator.handleEventStreamInitEvent(new EventStreamCreatedEvent(stream));

        assertThat(validator)
                .extracting("eventstreams", InstanceOfAssertFactories.collection(EventStream.class))
                .doesNotContain(stream);
        assertThat(validator)
                .extracting("closedEventstreams", InstanceOfAssertFactories.collection(String.class))
                .contains(stream.getCollection());
    }

    static class IncorrectMemberArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of("example-ldes-member.nq", STATE,
                            List.of("Member must have exactly 0 statements with timestamp path: " + TIMESTAMP_PATH,
                                    "Member must have exactly 0 statements with versionOf path: " + VERSIONOF_PATH)),
                    Arguments.of("example-ldes-member-multiple-version-ofs.nq", STATE,
                            List.of("Member must have exactly 0 statements with timestamp path: " + TIMESTAMP_PATH,
                                    "Member must have exactly 0 statements with versionOf path: " + VERSIONOF_PATH)),
                    Arguments.of("example-ldes-member-without-root-timestamp.nq", VERSION,
                            List.of("Member must have exactly 1 statement with timestamp path: " + TIMESTAMP_PATH)),
                    Arguments.of("example-ldes-member-multiple-version-ofs.nq", VERSION,
                            List.of("Member must have exactly 1 statement with versionOf path: " + VERSIONOF_PATH)),
                    Arguments.of("example-ldes-member-wrong-type-version-of.nq", VERSION,
                            List.of("Object of statement with predicate: " + VERSIONOF_PATH + " should be a resource")),
                    Arguments.of("example-ldes-member-wrong-type-timestamp.nq", VERSION,
                            List.of("Object of statement with predicate: " + TIMESTAMP_PATH + " should be a literal either of type " + XSDDatatype.XSDdateTime.getURI() + " or " + XSDDatatype.XSDstring.getURI())),
                    Arguments.of("example-ldes-member-dangling-nodes.nq", VERSION, List.of("Object graphs don't allow blank nodes to occur outside of a named object.")),
                    Arguments.of("example-ldes-member-blank-node.nq", VERSION, List.of("Object graphs don't allow blank nodes to occur outside of a named object.")),
                    Arguments.of("example-ldes-member-shared-blank-node.nq", VERSION, List.of("Blank nodes must be scoped to one object.")));
        }
    }

    static class CorrectMemberArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(RDFDataMgr.loadModel("example-ldes-member-state.nq"), STATE),
                    Arguments.of(RDFDataMgr.loadModel("example-ldes-member-typeless-time.nq"), VERSION),
                    Arguments.of(RDFDataMgr.loadModel("example-ldes-member-string-time.nq"), VERSION),
                    Arguments.of(RDFDataMgr.loadModel("example-ldes-member.nq"), VERSION),
                    Arguments.of(RDFDataMgr.loadModel("example-ldes-member-without-version-of.nq"), VERSION)
            );
        }
    }

    private Condition<String> errorMessages(List<String> expectedMessages) {
        return new Condition<>(actual -> expectedMessages.stream().allMatch(actual::contains), "contained by %s".formatted(expectedMessages));
    }

}