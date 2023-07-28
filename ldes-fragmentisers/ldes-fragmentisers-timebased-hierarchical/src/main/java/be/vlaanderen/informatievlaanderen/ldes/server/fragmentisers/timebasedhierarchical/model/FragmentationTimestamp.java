package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;

import java.time.LocalDateTime;
import java.util.Objects;

public class FragmentationTimestamp {
	private final LocalDateTime time;
	private final Granularity granularity;

	public FragmentationTimestamp(LocalDateTime time, Granularity granularity) {
		this.time = time;
		this.granularity = granularity;
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
