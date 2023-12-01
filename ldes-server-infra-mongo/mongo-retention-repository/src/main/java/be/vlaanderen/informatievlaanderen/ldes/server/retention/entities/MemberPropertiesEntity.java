package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document(MemberPropertiesEntity.NAME)
@CompoundIndex(name = "versionOf_view", def = "{'views' : 1, 'versionOf': 1}")
public class MemberPropertiesEntity {

	public static final String NAME = "retention_member_properties";

	@Id
	private final String id;
	@Indexed
	private final String collectionName;
	@Indexed
	private final Set<String> views;
	private final String versionOf;
	private final LocalDateTime timestamp;

	public MemberPropertiesEntity(String id, String collectionName, Set<String> views, String versionOf,
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

	public Set<String> getViews() {
		return views;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
