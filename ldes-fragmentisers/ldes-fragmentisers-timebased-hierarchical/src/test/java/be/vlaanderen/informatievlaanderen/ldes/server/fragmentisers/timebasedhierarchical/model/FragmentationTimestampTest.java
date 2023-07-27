package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
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
	}

}
