package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EventStreamResponseTest {
	private static final String COLLECTION = "collection";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final String MEMBER_TYPE_PATH = "memberType";
	private static final boolean HAS_DEFAULT_VIEW = false;
	private static final EventStreamResponse EVENT_STREAM_RESPONSE = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH,
			VERSION_OF_PATH, MEMBER_TYPE_PATH, HAS_DEFAULT_VIEW, List.of(), ModelFactory.createDefaultModel());
	private static final EventStreamResponse EVENT_STREAM_RESPONSE_WITH_DATASET = new EventStreamResponse(COLLECTION,
			TIMESTAMP_PATH,
			VERSION_OF_PATH, MEMBER_TYPE_PATH, HAS_DEFAULT_VIEW, List.of(), ModelFactory.createDefaultModel(),
			ModelFactory.createDefaultModel());

	@Test
	void test_equality() {
		EventStreamResponse other = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
				MEMBER_TYPE_PATH, HAS_DEFAULT_VIEW, List.of(),
				ModelFactory.createDefaultModel());

		assertEquals(EVENT_STREAM_RESPONSE, other);
		assertEquals(EVENT_STREAM_RESPONSE, EVENT_STREAM_RESPONSE);
	}

	@Test
	void test_equality_with_dataset() {
		EventStreamResponse other = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
				MEMBER_TYPE_PATH, HAS_DEFAULT_VIEW, List.of(),
				ModelFactory.createDefaultModel(), ModelFactory.createDefaultModel());

		assertEquals(EVENT_STREAM_RESPONSE_WITH_DATASET, other);
		assertEquals(EVENT_STREAM_RESPONSE_WITH_DATASET, EVENT_STREAM_RESPONSE_WITH_DATASET);
	}

	@ParameterizedTest
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
					new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE_PATH,
							HAS_DEFAULT_VIEW, List.of(),
							ModelFactory.createDefaultModel().add(ResourceFactory.createResource(),
									RdfConstants.IS_PART_OF_PROPERTY, "object")),
					new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, "other", MEMBER_TYPE_PATH, HAS_DEFAULT_VIEW,
							List.of(),
							ModelFactory.createDefaultModel()),
					new EventStreamResponse(COLLECTION, "other", VERSION_OF_PATH, MEMBER_TYPE_PATH, HAS_DEFAULT_VIEW,
							List.of(),
							ModelFactory.createDefaultModel()),
					new EventStreamResponse("other", TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE_PATH,
							HAS_DEFAULT_VIEW, List.of(),
							ModelFactory.createDefaultModel()),
					new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, "other", HAS_DEFAULT_VIEW,
							List.of(),
							ModelFactory.createDefaultModel()),
					new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, "other", false, List.of(),
							ModelFactory.createDefaultModel()),
					null,
					EVENT_STREAM_RESPONSE_WITH_DATASET,
					new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE_PATH, HAS_DEFAULT_VIEW))
					.map(Arguments::of);
		}
	}
}