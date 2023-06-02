package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities;

import java.util.Objects;

public class EventStream {

	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final String memberType;
	private final boolean defaultViewEnabled;

	public EventStream(String collection, String timestampPath, String versionOfPath, String memberType,
			boolean defaultViewEnabled) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.memberType = memberType;
		this.defaultViewEnabled = defaultViewEnabled;
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

	public String getMemberType() {
		return memberType;
	}

	public boolean isDefaultViewEnabled() {
		return defaultViewEnabled;
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
