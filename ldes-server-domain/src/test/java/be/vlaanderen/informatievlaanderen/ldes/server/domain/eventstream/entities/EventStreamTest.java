package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EventStreamTest {
	private static final String COLLECTION = "collection_name";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final String MEMBER_TYPE = "memberType";
	private static final boolean HAS_DEFAULT_VIEW = false;

	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
			MEMBER_TYPE, HAS_DEFAULT_VIEW);

	@Test
	void test_inequality() {
		final EventStream other = new EventStream("other", TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE,
				HAS_DEFAULT_VIEW);

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
					new EventStream(COLLECTION, TIMESTAMP_PATH, "other", MEMBER_TYPE, HAS_DEFAULT_VIEW),
					new EventStream(COLLECTION, "other", VERSION_OF_PATH, MEMBER_TYPE, HAS_DEFAULT_VIEW),
					new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE, HAS_DEFAULT_VIEW),
					new EventStream(COLLECTION, "other", "other", MEMBER_TYPE, HAS_DEFAULT_VIEW),
					new EventStream(COLLECTION, "other", "other", "other", false))
					.map(Arguments::of);
		}
	}
}