package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "eventstreams")
public class EventStreamEntity {
	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final String views;

	public EventStreamEntity(String id, String timestampPath, String versionOfPath, String views) {
		this.id = id;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.views = views;
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

	public String getViews() {
		return views;
	}
}
