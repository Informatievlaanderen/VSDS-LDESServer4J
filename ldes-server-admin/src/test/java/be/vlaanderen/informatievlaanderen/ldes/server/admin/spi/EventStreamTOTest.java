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
	private static final boolean CLOSED = false;
	private static final EventStreamTO EVENT_STREAM_RESPONSE = getBaseBuilder().build();
	private static final EventStreamTO EVENT_STREAM_RESPONSE_WITH_DATASET = getBaseBuilder()
			.withDcatDataset(new DcatDataset(COLLECTION, ModelFactory
					.createDefaultModel().add(createResource("subject"), createProperty("predicate"), "value")))
			.build();

	@Test
	void test_equality() {
		EventStreamTO other = getBaseBuilder().build();

		assertEquals(EVENT_STREAM_RESPONSE, other);
	}

	@Test
	void test_equality_with_dataset() {
		DcatDataset dcatDataset = new DcatDataset(COLLECTION, ModelFactory.createDefaultModel().add(createResource("subject"),
				createProperty("predicate"), "value"));
		EventStreamTO other = getBaseBuilder().withDcatDataset(dcatDataset).build();

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
							getBaseBuilder()
									.withShacl(ModelFactory.createDefaultModel().add(createResource(), RdfConstants.IS_PART_OF_PROPERTY, "object"))
									.build(),
					Arguments.of("VersionPath",
							getBaseBuilder().withVersionOfPath("other").build()),
					Arguments.of("timestampPath",
							getBaseBuilder().withTimestampPath("other").build()),
					Arguments.of("collection",
							getBaseBuilder().withCollection("other").build()),
					Arguments.of("null", null),
					Arguments.of("dataset", EVENT_STREAM_RESPONSE_WITH_DATASET),
					Arguments.of(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED, null))));
		}
	}

	private static EventStreamTO.Builder getBaseBuilder() {
		return new EventStreamTO.Builder()
				.withCollection(COLLECTION)
				.withTimestampPath(TIMESTAMP_PATH)
				.withVersionOfPath(VERSION_OF_PATH)
				.withVersionCreationEnabled(VERSION_CREATION_ENABLED)
				.withClosed(CLOSED)
				.withShacl(ModelFactory.createDefaultModel())
				.withEventSourceRetentionPolicies(List.of());
	}

}