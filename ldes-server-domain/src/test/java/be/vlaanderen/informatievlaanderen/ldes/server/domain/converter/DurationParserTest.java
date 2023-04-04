package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DurationParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationParserTest {

	@Test
	void when_StringIsDuration() {
		Duration duration = Duration.ofMinutes(4000);
		String durationString = duration.toString();
		Duration toTest = DurationParser.parseText(durationString);
		Assertions.assertEquals(duration, toTest);

	}

	@Test
	void when_StringIsPeriod() {
		Duration duration = Duration.ofDays(30);
		Period period = Period.ofMonths(1);
		String periodString = period.toString();
		Duration toTest = DurationParser.parseText(periodString);
		Assertions.assertEquals(duration, toTest);
	}

	@Test()
	void when_StringIsInvalid() {
		String invalidString = "PT5F9";
		assertThrows(DurationParserException.class, () -> DurationParser.parseText(invalidString));
	}
}
