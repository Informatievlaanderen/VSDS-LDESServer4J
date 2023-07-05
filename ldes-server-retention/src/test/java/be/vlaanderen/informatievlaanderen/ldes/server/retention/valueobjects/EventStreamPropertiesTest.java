package be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventStreamPropertiesTest {

	private final static String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
	private final static String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";

	@Test
	void test_equality() {
		EventStreamProperties eventStreamProperties = new EventStreamProperties(VERSION_OF_PATH,
				TIMESTAMP_PATH);
		EventStreamProperties otherEventStreamProperties = new EventStreamProperties(VERSION_OF_PATH,
				TIMESTAMP_PATH);
		assertEquals(eventStreamProperties, otherEventStreamProperties);
		assertEquals(eventStreamProperties, eventStreamProperties);
		assertEquals(otherEventStreamProperties, otherEventStreamProperties);

		assertEquals(eventStreamProperties.hashCode(), otherEventStreamProperties.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(EventStreamPropertiesArgumentsProvider.class)
	void test_inequality(Object otherEventStreamProperties) {
		EventStreamProperties eventStreamProperties = new EventStreamProperties(VERSION_OF_PATH,
				TIMESTAMP_PATH);
		assertNotEquals(eventStreamProperties, otherEventStreamProperties);
		if (otherEventStreamProperties != null) {
			assertNotEquals(eventStreamProperties.hashCode(), otherEventStreamProperties.hashCode());
		}
	}

	static class EventStreamPropertiesArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(new EventStreamProperties(VERSION_OF_PATH, "other")),
					Arguments.of(new EventStreamProperties("other", TIMESTAMP_PATH)),
					Arguments.of((Object) null),
					Arguments.of(new BigDecimal(1)));
		}
	}
}
