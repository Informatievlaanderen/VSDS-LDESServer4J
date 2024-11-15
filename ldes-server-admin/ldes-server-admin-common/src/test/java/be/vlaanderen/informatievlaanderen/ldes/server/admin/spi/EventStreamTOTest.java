package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions.InvalidSkolemisationDomainException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStreamTOTest {
	private static final String COLLECTION = "collection";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final boolean CLOSED = false;
	private static final EventStreamTO EVENT_STREAM_RESPONSE = getBaseBuilder().build();
	private static final EventStreamTO EVENT_STREAM_RESPONSE_WITH_DATASET = getBaseBuilder()
			.withDcatDataset(new DcatDataset(COLLECTION, ModelFactory
					.createDefaultModel().add(createResource("subject"), createProperty("predicate"), "value")))
			.build();

	@Test
	void test_equality() {
		EventStreamTO other = getBaseBuilder().build();

		assertThat(other).isEqualTo(EVENT_STREAM_RESPONSE);
	}

	@Test
	void test_equality_with_dataset() {
		DcatDataset dcatDataset = new DcatDataset(COLLECTION, ModelFactory.createDefaultModel().add(createResource("subject"),
				createProperty("predicate"), "value"));
		EventStreamTO other = getBaseBuilder().withDcatDataset(dcatDataset).build();

		assertThat(other).isEqualTo(EVENT_STREAM_RESPONSE_WITH_DATASET);
	}

	@ParameterizedTest(name = "{0} is not equal")
	@ArgumentsSource(EventStreamResponseArgumentsProvider.class)
	void test_inEquality(Object other) {
		assertThat(other).isNotEqualTo(EVENT_STREAM_RESPONSE);

		if (other != null) {
			assertThat(other).doesNotHaveSameHashCodeAs(EVENT_STREAM_RESPONSE);
		}
	}

	@Test
	void test_invalid_skolem_domain() {
		var builder = getBaseBuilder().withSkolemizationDomain("example.com");
		assertThatThrownBy(builder::build)
				.isInstanceOf(InvalidSkolemisationDomainException.class)
				.hasMessage("Invalid Skolemisation Domain. Should be URI. Provided skolemizationDomain : example.com");
	}

	@Test
	void test_kafka_props() {
		var eventStreamTO = getBaseBuilder()
				.withKafkaSourceProperties(new KafkaSourceProperties("collection", "topic", "mimeType"))
				.build();

		assertTrue(eventStreamTO.getKafkaSourceProperties().isPresent());
		var kafkaSourceProperties = eventStreamTO.getKafkaSourceProperties().get();
		assertEquals("collection", kafkaSourceProperties.collection());
		assertEquals("topic", kafkaSourceProperties.topic());
		assertEquals("mimeType", kafkaSourceProperties.mimeType());
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
							Arguments.of(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, null))));
		}
	}

	private static EventStreamTO.Builder getBaseBuilder() {
		return new EventStreamTO.Builder()
				.withCollection(COLLECTION)
				.withTimestampPath(TIMESTAMP_PATH)
				.withVersionOfPath(VERSION_OF_PATH)
				.withClosed(CLOSED)
				.withShacl(ModelFactory.createDefaultModel())
				.withEventSourceRetentionPolicies(List.of());
	}

}