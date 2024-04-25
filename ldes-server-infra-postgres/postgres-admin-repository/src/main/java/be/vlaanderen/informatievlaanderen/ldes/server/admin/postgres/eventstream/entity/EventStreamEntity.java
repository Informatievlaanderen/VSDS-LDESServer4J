package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "eventstream")
public class EventStreamEntity {
	@Id
	private String id;
	private String timestampPath;
	private String versionOfPath;
	private boolean versionCreationEnabled;

	protected EventStreamEntity() {}

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
