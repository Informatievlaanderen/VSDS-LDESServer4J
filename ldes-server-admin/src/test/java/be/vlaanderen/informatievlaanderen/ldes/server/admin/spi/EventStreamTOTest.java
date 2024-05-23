package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EventStreamTOTest {
    private static final String COLLECTION = "collection";
    private static final String TIMESTAMP_PATH = "generatedAt";
    private static final String VERSION_OF_PATH = "isVersionOf";
    private static final boolean VERSION_CREATION_ENABLED = false;
    private static final EventStreamTO EVENT_STREAM_RESPONSE = new EventStreamTO(COLLECTION, TIMESTAMP_PATH,
            VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of(), ModelFactory.createDefaultModel(), List.of());
    private static final EventStreamTO EVENT_STREAM_RESPONSE_WITH_DATASET = new EventStreamTO(COLLECTION,
            TIMESTAMP_PATH,
            VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of(), ModelFactory.createDefaultModel(),
            List.of(), new DcatDataset(COLLECTION, ModelFactory.createDefaultModel().add(createResource("subject"),
                    createProperty("predicate"), "value")));

    @Test
    void test_equality() {
        EventStreamTO other = new EventStreamTO(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of(),
                ModelFactory.createDefaultModel(), List.of());

        assertEquals(EVENT_STREAM_RESPONSE, other);
    }

    @Test
    void test_equality_with_dataset() {
        EventStreamTO other = new EventStreamTO(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED,
                List.of(),
                ModelFactory.createDefaultModel(), List.of(),
                new DcatDataset(COLLECTION, ModelFactory.createDefaultModel().add(createResource("subject"),
                        createProperty("predicate"), "value")));

        assertEquals(EVENT_STREAM_RESPONSE_WITH_DATASET, other);
    }

    @ParameterizedTest(name = "{0} is not equal")
    @ArgumentsSource(EventStreamResponseArgumentsProvider.class)
    void test_inEquality(Object other) {
        assertNotEquals(EVENT_STREAM_RESPONSE, other);

        if (other != null) {
            assertNotEquals(EVENT_STREAM_RESPONSE.hashCode(), other.hashCode());
        }
    }

    static class EventStreamResponseArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of("Shacl",
                            new EventStreamTO(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED,
                                    List.of(),
                                    ModelFactory.createDefaultModel().add(createResource(),
                                            RdfConstants.IS_PART_OF_PROPERTY, "object"), List.of())),
                    Arguments.of("VersionPath",
                            new EventStreamTO(COLLECTION, TIMESTAMP_PATH, "other", VERSION_CREATION_ENABLED,
                                    List.of(),
                                    ModelFactory.createDefaultModel(), List.of())),
                    Arguments.of("timestampPath",
                            new EventStreamTO(COLLECTION, "other", VERSION_OF_PATH, VERSION_CREATION_ENABLED,
                                    List.of(),
                                    ModelFactory.createDefaultModel(), List.of())),
                    Arguments.of("collection",
                            new EventStreamTO("other", TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED,
                                    List.of(),
                                    ModelFactory.createDefaultModel(), List.of())),
                    Arguments.of("HasDefaultView",
                            new EventStreamTO(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED,
                                    List.of(),
                                    ModelFactory.createDefaultModel(), List.of())),
                    Arguments.of("null", null),
                    Arguments.of("dataset", EVENT_STREAM_RESPONSE_WITH_DATASET),
                    Arguments.of(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED)));
        }
    }

}