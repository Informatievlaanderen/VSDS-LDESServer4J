package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.*;

public class FragmentationTimestamp {
	private final Map<String, String> timeMap;
	private final String granularity;

	public FragmentationTimestamp(LocalDateTime time, String granularity) {
		this.timeMap = localDateTimeToMap(time);
		this.granularity = granularity;
	}

	public FragmentationTimestamp(Map<String, String> time) {
		this.timeMap = time;
		this.granularity = TimeBasedConstants.temporalFields.get(time.size() - 1);
	}

	public String getGranularity() {
		return granularity;
	}

	public int getGranularityNumber() {
		return temporalFields.indexOf(granularity);
	}

	public Map<String, String> getTemporalFieldsWithValues() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i <= getGranularityNumber(); i++) {
			map.put(temporalFields.get(i), timeMap.get(temporalFields.get(i)));
		}
		return map;
	}

	public String asString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i <= getGranularityNumber(); i++) {
			if (i == 1 || i == 2) {
				builder.append("-");
			} else if (i == 3) {
				builder.append("T");
			} else if (i != 0) {
				builder.append(":");
			}
			builder.append(timeMap.get(temporalFields.get(i)));
		}
		return builder.toString();
	}

	private Map<String, String> localDateTimeToMap(LocalDateTime time) {
		Map<String, String> map = new HashMap<>();
		map.put(YEAR, String.valueOf(time.getYear()));
		map.put(MONTH, String.valueOf(time.getMonthValue()));
		map.put(DAY, String.valueOf(time.getDayOfMonth()));
		map.put(HOUR, String.valueOf(time.getHour()));
		map.put(MINUTE, String.valueOf(time.getMinute()));
		map.put(SECOND, String.valueOf(time.getSecond()));
		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FragmentationTimestamp that = (FragmentationTimestamp) o;
		return timeMap.equals(that.timeMap) && granularity.equals(that.granularity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(timeMap, granularity);
	}
}
