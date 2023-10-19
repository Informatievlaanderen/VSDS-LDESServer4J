package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.exceptions.FragmentiserConfigException;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

public enum Granularity {

	SECOND("second", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss"), DateTimeFormatter.ofPattern("ss"),
			"http://www.w3.org/2001/XMLSchema#dateTime", null),
	MINUTE("minute", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm"), DateTimeFormatter.ofPattern("mm"),
			"http://www.w3.org/2001/XMLSchema#string", Granularity.SECOND),
	HOUR("hour", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh"), DateTimeFormatter.ofPattern("hh"),
			"http://www.w3.org/2001/XMLSchema#string", Granularity.MINUTE),
	DAY("day", DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("dd"),
			"http://www.w3.org/2001/XMLSchema#date", Granularity.HOUR),
	MONTH("month", DateTimeFormatter.ofPattern("yyyy-MM"), DateTimeFormatter.ofPattern("MM"),
			"http://www.w3.org/2001/XMLSchema#gYearMonth", Granularity.DAY),
	YEAR("year", DateTimeFormatter.ofPattern("yyyy"), DateTimeFormatter.ofPattern("yyyy"),
			"http://www.w3.org/2001/XMLSchema#gYear", Granularity.MONTH);

	private final String value;
	private final DateTimeFormatter formatter;
	private final DateTimeFormatter getTimeValue;
	private final String type;
	private final Granularity child;

	Granularity(String value, DateTimeFormatter formatter, DateTimeFormatter getTimeValue, String type, Granularity child) {
		this.value = value;
		this.formatter = formatter;
		this.getTimeValue = getTimeValue;
		this.type = type;
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

	public String getType() {
		return type;
	}

}
