package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DurationParserException;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;

public class DurationParser {

	private static final long NUMBER_OF_DAYS_PER_MONTH = 30L;
	private static final long NUMBER_OF_DAYS_PER_YEAR = 365L;

	private Duration periodToDuration(Period period) {
		int days = period.getDays();
		int months = period.getMonths();
		int years = period.getYears();
		return Duration.ofDays(days + (months * NUMBER_OF_DAYS_PER_MONTH) + (years * NUMBER_OF_DAYS_PER_YEAR));
	}

	public Duration parseText(String duration) {
		try {
			return Duration.parse(duration);
		} catch (DateTimeParseException ignored) {
			// if text cannot be parsed to duration, try parsing it to period
			try {
				Period period = Period.parse(duration);
				return periodToDuration(period);
			} catch (DateTimeParseException e) {
				throw new DurationParserException(duration);
			}
		}
	}

}
