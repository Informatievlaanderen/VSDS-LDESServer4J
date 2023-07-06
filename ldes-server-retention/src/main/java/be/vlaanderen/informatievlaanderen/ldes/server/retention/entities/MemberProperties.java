package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import java.time.LocalDateTime;
import java.util.List;

public class MemberProperties {
	private final String id;
	private final String collectionName;
	private final List<String> views;
	private final String versionOf;
	private final LocalDateTime timestamp;

	public MemberProperties(String id, String collectionName, List<String> views, String versionOf,
			LocalDateTime timestamp) {
		this.id = id;
		this.collectionName = collectionName;
		this.views = views;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public List<String> getViews() {
		return views;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
