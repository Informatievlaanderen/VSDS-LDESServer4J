package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FragmentationTimestampTest {

	private static final LocalDateTime TIME6am = LocalDateTime.of(2023, 1, 2, 6, 30, 40);
	private static final LocalDateTime TIME6pm = LocalDateTime.of(2023, 1, 2, 18, 30, 40);

	@ParameterizedTest
	@MethodSource("provideArgumentsForNextUpdateTs")
	void testGetNextUpdateTs(LocalDateTime time, Granularity granularity, LocalDateTime expectedNextUpdateTs) {
		assertThat(new FragmentationTimestamp(time, granularity).getNextUpdateTs()).isEqualTo(expectedNextUpdateTs);
	}

	private static Stream<Arguments> provideArgumentsForNextUpdateTs() {
		final int tsYear = 2023;
		final Month tsMonth = Month.JANUARY;
		final int tsDay = 15;
		final int tsHour = 10;
		final int tsMinute = 30;
		final LocalDateTime timestamp = LocalDateTime.of(tsYear, tsMonth, tsDay, tsHour, tsMinute);
		return Stream.of(
				Arguments.of(timestamp, Granularity.YEAR, LocalDateTime.of(tsYear, Month.DECEMBER, 31, 23, 59, 59)),
				Arguments.of(timestamp, Granularity.MONTH, LocalDateTime.of(tsYear, tsMonth, 31, 23, 59, 59)),
				Arguments.of(LocalDateTime.of(tsYear, Month.FEBRUARY, tsDay, tsHour, tsMinute),
						Granularity.MONTH, LocalDateTime.of(tsYear, Month.FEBRUARY, 28, 23, 59, 59)),
				Arguments.of(timestamp, Granularity.DAY, LocalDateTime.of(tsYear, tsMonth, tsDay, 23, 59, 59)),
				Arguments.of(timestamp, Granularity.HOUR, LocalDateTime.of(tsYear, tsMonth, tsDay, tsHour, 59, 59)),
				Arguments.of(timestamp, Granularity.MINUTE, LocalDateTime.of(tsYear, tsMonth, tsDay, tsHour, tsMinute, 59)),
				Arguments.of(timestamp, Granularity.SECOND, null)
		);
	}

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
					Arguments.of(TIME6am, Granularity.HOUR, "2023-01-02T06:00:00"),
					Arguments.of(TIME6am, Granularity.MINUTE, "2023-01-02T06:30:00"),
					Arguments.of(TIME6am, Granularity.SECOND, "2023-01-02T06:30:40"),
					Arguments.of(TIME6pm, Granularity.SECOND, "2023-01-02T18:30:40"));
		}
	}

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
