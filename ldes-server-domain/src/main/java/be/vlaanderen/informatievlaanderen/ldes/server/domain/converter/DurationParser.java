package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DurationParserException;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;

public class DurationParser {

	private static Duration periodToDuration(Period period) {
		int days = period.getDays();
		int months = period.getMonths();
		int years = period.getYears();
		return Duration.ofDays(days + (months * 30L) + (years * 365L));
	}

	public static Duration parseText(String duration) {
		try {
			return Duration.parse(duration);
		} catch (DateTimeParseException ignored) {
			// if text cannot be parsed to duration, try parsing it to period
		}
		try {
			Period period = Period.parse(duration);
			return periodToDuration(period);
		} catch (DateTimeParseException e) {
			throw new DurationParserException(duration);
		}
	}

}
