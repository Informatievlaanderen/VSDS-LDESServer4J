package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("ldesmember")
public class LdesMemberEntityV3 {

	@Id
	private final String id;
	@Indexed
	private final String versionOf;
	@Indexed
	private final LocalDateTime timestamp;
	private final String model;
	@Indexed
	private final List<String> treeNodeReferences;

	public LdesMemberEntityV3(String id, String versionOf, LocalDateTime timestamp, final String model,
			List<String> treeNodeReferences) {
		this.id = id;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.model = model;
		this.treeNodeReferences = treeNodeReferences;
	}

	public String getModel() {
		return this.model;
	}

	public String getId() {
		return id;
	}

	public List<String> getTreeNodeReferences() {
		return treeNodeReferences;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
