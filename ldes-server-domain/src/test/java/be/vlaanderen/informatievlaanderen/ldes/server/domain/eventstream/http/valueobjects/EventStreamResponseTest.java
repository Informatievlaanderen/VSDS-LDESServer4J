package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
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
	private static final EventStreamResponse EVENT_STREAM_RESPONSE = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH,
			VERSION_OF_PATH, List.of(), ModelFactory.createDefaultModel());

	@Test
	void test_equality() {
		EventStreamResponse other = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, List.of(),
				ModelFactory.createDefaultModel());

		assertEquals(EVENT_STREAM_RESPONSE, other);
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
					new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, List.of(),
							ModelFactory.createDefaultModel().add(ResourceFactory.createResource(),
									RdfConstants.IS_PART_OF_PROPERTY, "object")),
					new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, null,
							ModelFactory.createDefaultModel()),
					new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, "other", List.of(),
							ModelFactory.createDefaultModel()),
					new EventStreamResponse(COLLECTION, "other", VERSION_OF_PATH, List.of(),
							ModelFactory.createDefaultModel()),
					new EventStreamResponse("other", TIMESTAMP_PATH, VERSION_OF_PATH, List.of(),
							ModelFactory.createDefaultModel()),
					null,
					new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH)).map(Arguments::of);
		}
	}
}