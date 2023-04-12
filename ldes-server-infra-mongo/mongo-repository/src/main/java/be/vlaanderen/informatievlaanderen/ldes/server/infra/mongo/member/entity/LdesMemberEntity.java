package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("ldesmember")
public class LdesMemberEntity {

	@Id
	private final String id;
	@Indexed
	private final String collection;
	@Indexed
	private Long index;
	@Indexed
	private final String versionOf;
	@Indexed
	private final LocalDateTime timestamp;
	private final String model;
	@Indexed
	private final List<String> treeNodeReferences;

	public LdesMemberEntity(String id, String collection, Long index, String versionOf, LocalDateTime timestamp,
			final String model,
			List<String> treeNodeReferences) {
		this.id = id;
		this.collection = collection;
		this.index = index;
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

	public String getCollection() {
		return collection;
	}

	public Long getIndex() {
		return index;
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

	public void setIndex(long index) {
		this.index = index;
	}
}
