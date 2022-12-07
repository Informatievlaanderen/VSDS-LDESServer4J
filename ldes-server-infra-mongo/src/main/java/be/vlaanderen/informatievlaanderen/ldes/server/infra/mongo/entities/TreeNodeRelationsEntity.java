package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("relations")
public class TreeNodeRelationsEntity {
	@Id
	private final String id;

	private final List<TreeRelation> relations;

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public TreeNodeRelationsEntity(String id, List<TreeRelation> relations) {
		this.id = id;
		this.relations = relations;
	}

}
