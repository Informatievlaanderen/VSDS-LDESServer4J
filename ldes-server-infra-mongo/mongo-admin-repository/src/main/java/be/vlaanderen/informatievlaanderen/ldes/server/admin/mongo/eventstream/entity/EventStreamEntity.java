package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "eventstream")
public class EventStreamEntity {
	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final boolean versionCreationEnabled;

	public EventStreamEntity(String id, String timestampPath, String versionOfPath, boolean versionCreationEnabled) {
		this.id = id;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.versionCreationEnabled = versionCreationEnabled;
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

	public boolean isVersionCreationEnabled() {
		return versionCreationEnabled;
	}
}
