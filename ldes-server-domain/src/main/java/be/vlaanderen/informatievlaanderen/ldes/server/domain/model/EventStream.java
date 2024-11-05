package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import java.util.Objects;
import java.util.Optional;

public class EventStream {

	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final VersionCreationProperties versionCreationProperties;
	private final boolean isClosed;
	private final String skolemizationDomain;

	public EventStream(String collection,
	                   String timestampPath,
	                   String versionOfPath,
	                   VersionCreationProperties versionCreationProperties) {
		this(collection, timestampPath, versionOfPath, versionCreationProperties, false, null);
	}

	public EventStream(String collection,
	                   String timestampPath,
	                   String versionOfPath,
	                   VersionCreationProperties versionCreationProperties,
	                   String skolemizationDomain) {
		this(collection, timestampPath, versionOfPath, versionCreationProperties, false, skolemizationDomain);
	}

	public EventStream(String collection,
	                   String timestampPath,
	                   String versionOfPath,
	                   VersionCreationProperties versionCreationProperties,
	                   boolean isClosed,
	                   String skolemizationDomain) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.versionCreationProperties = versionCreationProperties;
		this.isClosed = isClosed;
		this.skolemizationDomain = skolemizationDomain;
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

	public VersionCreationProperties getVersionCreationProperties() {
		return versionCreationProperties;
	}

	public String getVersionDelimiter() {
		return versionCreationProperties.getVersionDelimiter();
	}

	public boolean isVersionCreationEnabled() {
		return versionCreationProperties.isVersionCreationEnabled();
	}

	public boolean isClosed() {
		return isClosed;
	}

	public Optional<String> getSkolemizationDomain() {
		return Optional.ofNullable(skolemizationDomain);
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
