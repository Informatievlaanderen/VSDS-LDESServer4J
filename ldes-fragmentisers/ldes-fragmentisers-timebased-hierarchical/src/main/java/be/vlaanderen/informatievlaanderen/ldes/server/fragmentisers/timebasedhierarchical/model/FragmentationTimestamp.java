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
			case YEAR ->  time.with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59).withSecond(59);
			case MONTH -> time.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
			case DAY -> time.withHour(23).withMinute(59).withSecond(59);
			case HOUR -> time.withMinute(59).withSecond(59);
			case MINUTE -> time.withSecond(59);
			case SECOND -> null;
		};
	}

	public LocalDateTime getLtBoundary() {
		return switch (granularity) {
			case YEAR ->  time.plusYears(1);
			case MONTH -> time.plusMonths(1);
			case DAY -> time.plusDays(1);
			case HOUR -> time.plusHours(1);
			case MINUTE -> time.plusMinutes(1);
			case SECOND -> time.plusSeconds(1);
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
