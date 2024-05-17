package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "eventsource")
public class EventSourceEntity {
	@Id
	private String collectionName;
	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private List<String> retentionPolicies;

	protected EventSourceEntity() {}

	public EventSourceEntity(String collectionName, List<String> retentionPolicies) {
		this.collectionName = collectionName;
		this.retentionPolicies = retentionPolicies;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public List<String> getRetentionPolicies() {
		return retentionPolicies;
	}
}
