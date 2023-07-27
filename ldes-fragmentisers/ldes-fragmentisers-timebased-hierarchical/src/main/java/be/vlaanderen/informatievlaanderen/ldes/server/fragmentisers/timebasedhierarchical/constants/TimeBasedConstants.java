package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE;

public class TimeBasedConstants {

	private TimeBasedConstants() {
	}

	public static final String YEAR = "Y";
	public static final String MONTH = "M";
	public static final String DAY = "D";
	public static final String HOUR = "h";
	public static final String MINUTE = "m";
	public static final String SECOND = "s";
	public static final String TREE_INBETWEEN_RELATION = TREE + "InBetweenRelation";
	public static final String DATETIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	public static final List<String> temporalFields = Arrays.asList(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
	public static final List<Function<LocalDateTime, Integer>> tempFunctions = List.of(LocalDateTime::getYear,
			LocalDateTime::getMonthValue, LocalDateTime::getDayOfMonth, LocalDateTime::getHour,
			LocalDateTime::getMinute, LocalDateTime::getSecond);

}
