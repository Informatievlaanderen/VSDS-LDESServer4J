package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.exceptions.FragmentiserConfigException;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

public enum Granularity {

	SECOND("second", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"), DateTimeFormatter.ofPattern("ss"), null),
	MINUTE("minute", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00"), DateTimeFormatter.ofPattern("mm"), Granularity.SECOND),
	HOUR("hour", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00"), DateTimeFormatter.ofPattern("HH"), Granularity.MINUTE),
	DAY("day", DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("dd"), Granularity.HOUR),
	MONTH("month", DateTimeFormatter.ofPattern("yyyy-MM"), DateTimeFormatter.ofPattern("MM"), Granularity.DAY),
	YEAR("year", DateTimeFormatter.ofPattern("yyyy"), DateTimeFormatter.ofPattern("yyyy"), Granularity.MONTH);

	private final String value;
	private final DateTimeFormatter formatter;
	private final DateTimeFormatter getTimeValue;
	private final Granularity child;

	Granularity(String value, DateTimeFormatter formatter, DateTimeFormatter getTimeValue, Granularity child) {
		this.value = value;
		this.formatter = formatter;
		this.getTimeValue = getTimeValue;
		this.child = child;
	}

	public static Granularity from(String granularity) {
		return Stream.of(values()).filter(val -> val.getValue().equals(granularity))
				.findFirst().orElseThrow(() -> createFragmentiserConfigExceptionSupplier(granularity));
	}

	public static Granularity fromIndex(int index) {
		if (index < 0 || index > 5) {
			throw new IndexOutOfBoundsException("Granularity index must be between 0 and 5");
		}
		Granularity granularity = Granularity.YEAR;
		for (int i = 0; i < index; i++) {
			granularity = granularity.child;
		}
		return granularity;
	}

	private static FragmentiserConfigException createFragmentiserConfigExceptionSupplier(String granularity) {
		return new FragmentiserConfigException(
				granularity + " is not allowed. Allowed values are: " + Arrays.toString(values()));
	}

	public String getValue() {
		return value;
	}

	public DateTimeFormatter getFormatter() {
		return formatter;
	}

	public DateTimeFormatter getGetTimeValue() {
		return getTimeValue;
	}

	public Granularity getChild() {
		return child;
	}

}
