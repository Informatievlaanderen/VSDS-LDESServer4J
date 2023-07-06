package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("member_properties")
public class MemberPropertiesEntity {
	@Indexed
	private final String id;
	@Indexed
	private final String collectionName;
	@Indexed
	private final String versionOf;
	@Indexed
	private final LocalDateTime timestamp;

	public MemberPropertiesEntity(String id, String collectionName, String versionOf, LocalDateTime timestamp) {
		this.id = id;
		this.collectionName = collectionName;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
