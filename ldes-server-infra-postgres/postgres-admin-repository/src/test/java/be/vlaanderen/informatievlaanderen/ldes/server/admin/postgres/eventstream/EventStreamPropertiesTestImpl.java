package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.projection.EventStreamProperties;

public class EventStreamPropertiesTestImpl implements EventStreamProperties {
	private final String name;
	private final String timestampPath;
	private final String versionOfPath;
	private final String versionDelimiter;
	private final boolean closed;
	private final String skolemizationDomain;

	public EventStreamPropertiesTestImpl(String name, String timestampPath, String versionOfPath, String versionDelimiter, boolean closed, String skolemizationDomain) {
		this.name = name;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.versionDelimiter = versionDelimiter;
		this.closed = closed;
		this.skolemizationDomain = skolemizationDomain;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTimestampPath() {
		return timestampPath;
	}

	@Override
	public String getVersionOfPath() {
		return versionOfPath;
	}

	@Override
	public String getVersionDelimiter() {
		return versionDelimiter;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public String getSkolemizationDomain() {
		return skolemizationDomain;
	}
}
