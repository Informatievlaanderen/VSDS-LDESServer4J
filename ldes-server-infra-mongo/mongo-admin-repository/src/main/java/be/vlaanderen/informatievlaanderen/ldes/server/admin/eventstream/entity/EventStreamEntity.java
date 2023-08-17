package be.vlaanderen.informatievlaanderen.ldes.server.admin.eventstream.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "eventstreams")
public class EventStreamEntity {
	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final String memberType;

	public EventStreamEntity(String id, String timestampPath, String versionOfPath, String memberType) {
		this.id = id;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.memberType = memberType;
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
}
