package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset6.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset6.entities.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset6.entities.valueobjects.TreeRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ldesfragment")
public class LdesFragmentEntityV4 {
	@Id
	private final String id;
	@Indexed
	private final Boolean root;
	@Indexed
	private final String viewName;
	private final List<FragmentPair> fragmentPairs;
	@Indexed
	private final Boolean immutable;
	@Indexed
	private final String parentId;
	private final Integer numberOfMembers;
	private final List<TreeRelation> relations;
	@Indexed
	private final String collectionName;

	public LdesFragmentEntityV4(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
			Boolean immutable,
			String parentId, Integer numberOfMembers,
			List<TreeRelation> relations, String collectionName) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.parentId = parentId;
		this.numberOfMembers = numberOfMembers;
		this.relations = relations;
		this.collectionName = collectionName;
	}

	public String getId() {
		return id;
	}

	public Boolean getRoot() {
		return root;
	}

	public String getViewName() {
		return viewName;
	}

	public List<FragmentPair> getFragmentPairs() {
		return fragmentPairs;
	}

	public Boolean getImmutable() {
		return immutable;
	}

	public String getParentId() {
		return parentId;
	}

	public Integer getNumberOfMembers() {
		return numberOfMembers;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public String getCollectionName() {
		return collectionName;
	}
}
