package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "eventstreams")
public class EventStreamEntity {
	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final String memberType;
	private final boolean defaultViewEnabled;

	public EventStreamEntity(String id, String timestampPath, String versionOfPath, String memberType,
			boolean defaultViewEnabled) {
		this.id = id;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.memberType = memberType;
		this.defaultViewEnabled = defaultViewEnabled;
	}

	public String getId() {
		return id;
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
}
