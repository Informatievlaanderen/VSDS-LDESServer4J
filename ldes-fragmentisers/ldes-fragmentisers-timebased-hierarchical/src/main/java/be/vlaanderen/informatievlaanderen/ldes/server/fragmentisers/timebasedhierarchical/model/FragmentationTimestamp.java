package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public class FragmentationTimestamp {
	private final LocalDateTime time;
	private final Granularity granularity;

	public FragmentationTimestamp(LocalDateTime time, Granularity granularity) {
		this.time = time;
		this.granularity = granularity;
	}

	public LocalDateTime getNextUpdateTs() {
		return switch (granularity) {
			case YEAR ->  LocalDateTime.now().with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59).withSecond(59);
			case MONTH -> LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
			case DAY -> LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
			case HOUR -> LocalDateTime.now().withMinute(59).withSecond(59);
			case MINUTE -> LocalDateTime.now().withSecond(59);
			case SECOND -> null;
		};
	}

	public Granularity getGranularity() {
		return granularity;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public String getTimeValueForGranularity(Granularity granularity) {
		return granularity.getGetTimeValue().format(time);
	}

	public String getType() {
		return granularity.getType();
	}

	public String asString() {
		return granularity.getFormatter().format(time);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FragmentationTimestamp that = (FragmentationTimestamp) o;
		return time.equals(that.time) && granularity.equals(that.granularity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(time, granularity);
	}
}
