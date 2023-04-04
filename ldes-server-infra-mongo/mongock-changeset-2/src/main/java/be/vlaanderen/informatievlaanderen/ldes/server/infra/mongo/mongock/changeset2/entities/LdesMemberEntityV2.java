package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ldesmember")
public class LdesMemberEntityV2 {

	@Id
	private final String id;
	private final String model;
	@Indexed
	private final List<String> treeNodeReferences;

	public LdesMemberEntityV2(String id, final String model, List<String> treeNodeReferences) {
		this.id = id;
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
}
