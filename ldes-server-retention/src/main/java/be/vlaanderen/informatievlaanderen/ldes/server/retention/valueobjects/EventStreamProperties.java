package be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects;

import java.util.Objects;

public class EventStreamProperties {

	private final String versionOfPath;
	private final String timestampPath;

	public EventStreamProperties(String versionOfPath, String timestampPath) {
		this.versionOfPath = versionOfPath;
		this.timestampPath = timestampPath;
	}

	public String getVersionOfPath() {
		return versionOfPath;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventStreamProperties that = (EventStreamProperties) o;
		return Objects.equals(versionOfPath, that.versionOfPath) && Objects.equals(timestampPath, that.timestampPath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(versionOfPath, timestampPath);
	}
}
