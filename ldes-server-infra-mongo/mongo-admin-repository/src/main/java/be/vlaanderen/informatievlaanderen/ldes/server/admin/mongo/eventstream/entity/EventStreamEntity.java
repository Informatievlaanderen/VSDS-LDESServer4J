package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "eventstream")
public class EventStreamEntity {
	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final boolean versionCreationEnabled;
	private final List<String> retentionPolicies;

	public EventStreamEntity(String id, String timestampPath, String versionOfPath, boolean versionCreationEnabled, List<String> retentionPolicies) {
		this.id = id;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.versionCreationEnabled = versionCreationEnabled;
        this.retentionPolicies = retentionPolicies;
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

	public List<String> getRetentionPolicies() {
		return retentionPolicies;
	}
}
