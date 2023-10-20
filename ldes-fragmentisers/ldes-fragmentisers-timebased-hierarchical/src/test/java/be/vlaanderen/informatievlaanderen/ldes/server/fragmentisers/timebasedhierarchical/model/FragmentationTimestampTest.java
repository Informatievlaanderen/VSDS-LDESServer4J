package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FragmentationTimestampTest {

	private static final LocalDateTime TIME6am = LocalDateTime.of(2023, 1, 2, 6, 30, 40);
	private static final LocalDateTime TIME6pm = LocalDateTime.of(2023, 1, 2, 18, 30, 40);

	@ParameterizedTest(name = "test asString for granularity {0}")
	@ArgumentsSource(TimeArgumentProvider.class)
	void when_AsStringForGranularity_Then_ReturnCorrectString(LocalDateTime time,
															  Granularity granularity,
															  String expected) {
		FragmentationTimestamp fragmentationTimestamp = new FragmentationTimestamp(time, granularity);

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
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(TIME6am, Granularity.YEAR, "2023"),
					Arguments.of(TIME6am, Granularity.MONTH, "2023-01"),
					Arguments.of(TIME6am, Granularity.DAY, "2023-01-02"),
					Arguments.of(TIME6am, Granularity.HOUR, "2023-01-02T06"),
					Arguments.of(TIME6am, Granularity.MINUTE, "2023-01-02T06:30"),
					Arguments.of(TIME6am, Granularity.SECOND, "2023-01-02T06:30:40"),
					Arguments.of(TIME6pm, Granularity.SECOND, "2023-01-02T18:30:40"));
		}
	};

	static class EqualityTestProvider implements ArgumentsProvider {
		private static final FragmentationTimestamp time = new FragmentationTimestamp(
				LocalDateTime.of(2023, 1, 1, 0, 0, 0), Granularity.SECOND);

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), time, time),
					Arguments.of(equals(),
							new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0), Granularity.SECOND),
							time),
					Arguments.of(notEquals(),
							new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0), Granularity.DAY), time),
					Arguments.of(notEquals(),
							new FragmentationTimestamp(LocalDateTime.of(2023, 2, 1, 0, 0, 0), Granularity.SECOND),
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
