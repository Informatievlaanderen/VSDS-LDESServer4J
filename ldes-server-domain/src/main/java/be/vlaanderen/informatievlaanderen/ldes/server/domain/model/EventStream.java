package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import java.util.Objects;

public class EventStream {

	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final boolean versionCreationEnabled;

	public EventStream(String collection, String timestampPath, String versionOfPath, boolean versionCreationEnabled) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
        this.versionCreationEnabled = versionCreationEnabled;
    }

	public String getCollection() {
		return collection;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public String getVersionOfPath() {
		return versionOfPath;
	}

	public boolean isVersionCreationEnabled() {
		return versionCreationEnabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventStream that = (EventStream) o;
		return Objects.equals(collection, that.collection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection);
	}
}
