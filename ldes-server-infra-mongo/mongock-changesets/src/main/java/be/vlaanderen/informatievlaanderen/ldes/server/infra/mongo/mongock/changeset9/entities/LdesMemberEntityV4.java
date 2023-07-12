package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("ldesmember")
public class LdesMemberEntityV4 {

	@Id
	private final String id;
	@Indexed
	private final String collectionName;
	@Indexed
	private final String versionOf;
	@Indexed
	private final LocalDateTime timestamp;
	private final String model;
	@Indexed
	private final List<String> treeNodeReferences;
	@Indexed
	private Long sequenceNr;

	public LdesMemberEntityV4(String id, String collectionName, Long sequenceNr, String versionOf,
			LocalDateTime timestamp,
			final String model,
			List<String> treeNodeReferences) {
		this.id = id;
		this.sequenceNr = sequenceNr;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.model = model;
		this.treeNodeReferences = treeNodeReferences;
		this.collectionName = collectionName;
	}

	public String getModel() {
		return this.model;
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Long getSequenceNr() {
		return sequenceNr;
	}

	public void setSequenceNr(Long sequenceNr) {
		this.sequenceNr = sequenceNr;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public List<String> getTreeNodeReferences() {
		return treeNodeReferences;
	}
}