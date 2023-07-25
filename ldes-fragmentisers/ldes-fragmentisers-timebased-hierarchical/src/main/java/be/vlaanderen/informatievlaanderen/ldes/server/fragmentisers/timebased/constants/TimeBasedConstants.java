package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

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
	public static final String[] temporalFields = {YEAR, MONTH, DAY, HOUR, MINUTE, SECOND};

}
