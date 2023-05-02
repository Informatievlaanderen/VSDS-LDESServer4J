package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.junit.jupiter.api.Assertions.*;

class EventStreamTest {
	private static final String COLLECTION = "collection_name";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";

	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH);

	@Test
	void test_equality() {
		final EventStream other = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH);

		assertEquals(EVENT_STREAM, EVENT_STREAM);
		assertEquals(other, other);
		assertEquals(EVENT_STREAM, other);
	}

	@ParameterizedTest
	@ArgumentsSource(EventStreamArgumentProvider.class)
	void test_inequality(Object other) {
		assertNotEquals(EVENT_STREAM, other);
		if (other != null) {
			assertNotEquals(EVENT_STREAM.hashCode(), other.hashCode());
		}
	}

	static class EventStreamArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					new EventStream(COLLECTION, TIMESTAMP_PATH, "other"),
					new EventStream(COLLECTION, "other", VERSION_OF_PATH),
					new EventStream("other", TIMESTAMP_PATH, VERSION_OF_PATH),
					new EventStream("other", "other", "other"),
					null,
					createDefaultModel()).map(Arguments::of);
		}
	}
}