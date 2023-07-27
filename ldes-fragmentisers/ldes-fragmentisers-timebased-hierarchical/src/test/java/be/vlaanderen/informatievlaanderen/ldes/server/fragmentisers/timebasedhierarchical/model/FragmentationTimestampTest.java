package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.temporalFields;
import static org.junit.jupiter.api.Assertions.*;

class FragmentationTimestampTest {

	private static final LocalDateTime TIME = LocalDateTime.of(2023, 1, 2, 6, 30, 40);

	@ParameterizedTest(name = "test asString for granularity {0}")
	@ArgumentsSource(TimeArgumentProvider.class)
	void when_AsStringForGranularity_Then_ReturnCorrectString(String granularity, String expected) {
		FragmentationTimestamp fragmentationTimestamp = new FragmentationTimestamp(TIME, granularity);

		assertEquals(expected, fragmentationTimestamp.asString());
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, FragmentationTimestamp a,
			FragmentationTimestamp b) {
		assertNotNull(assertion);
		assertion.accept(a, b);
		if (a != null && b != null) {
			assertion.accept(a.hashCode(), b.hashCode());
		}
	}

	static class TimeArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of(temporalFields.get(0), "2023"),
					Arguments.of(temporalFields.get(1), "2023-1"),
					Arguments.of(temporalFields.get(2), "2023-1-2"),
					Arguments.of(temporalFields.get(3), "2023-1-2T6"),
					Arguments.of(temporalFields.get(4), "2023-1-2T6:30"),
					Arguments.of(temporalFields.get(5), "2023-1-2T6:30:40"));
		}
	};

	static class EqualityTestProvider implements ArgumentsProvider {

		private static final String idA = "idA";
		private static final FragmentationTimestamp time = new FragmentationTimestamp(
				LocalDateTime.of(2023, 1, 1, 0, 0, 0), "s");

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), time, time),
					Arguments.of(equals(), new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0), "s"),
							time),
					Arguments.of(notEquals(),
							new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0), "D"), time),
					Arguments.of(notEquals(), new FragmentationTimestamp(LocalDateTime.of(2023, 2, 1, 0, 0, 0), "s"),
							time));
		}

		private static BiConsumer<Object, Object> equals() {
			return Assertions::assertEquals;
		}

		private static BiConsumer<Object, Object> notEquals() {
			return Assertions::assertNotEquals;
		}

	}

}
