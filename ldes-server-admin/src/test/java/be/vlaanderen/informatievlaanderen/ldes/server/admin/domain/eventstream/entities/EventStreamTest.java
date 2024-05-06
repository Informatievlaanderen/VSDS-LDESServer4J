package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
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

class EventStreamTest {
	private static final String COLLECTION = "collection_name";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final boolean VERSION_CREATION_ENABLED = false;

	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of());

	@Test
	void test_inequality() {
		final EventStream other = new EventStream("other", TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of());

		assertNotEquals(EVENT_STREAM, other);
		assertNotEquals(null, EVENT_STREAM);
	}

	@ParameterizedTest
	@ArgumentsSource(EventStreamArgumentProvider.class)
	void test_equality(Object other) {
		assertEquals(EVENT_STREAM, other);
		if (other != null) {
			assertEquals(EVENT_STREAM.hashCode(), other.hashCode());
		}
	}

	static class EventStreamArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					EVENT_STREAM,
					new EventStream(COLLECTION, TIMESTAMP_PATH, "other", VERSION_CREATION_ENABLED, List.of()),
					new EventStream(COLLECTION, "other", VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of()),
					new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, true, List.of()),
					new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of()),
					new EventStream(COLLECTION, "other", "other", VERSION_CREATION_ENABLED, List.of()))
					.map(Arguments::of);
		}
	}
}