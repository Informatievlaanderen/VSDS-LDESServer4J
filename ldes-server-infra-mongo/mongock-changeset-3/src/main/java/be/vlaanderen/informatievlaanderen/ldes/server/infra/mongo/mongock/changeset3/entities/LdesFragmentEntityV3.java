package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.valueobjects.TreeRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("ldesfragment")
public class LdesFragmentEntityV3 {
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
	private final Boolean softDeleted;

	@Indexed
	private final String parentId;

	private final LocalDateTime immutableTimestamp;

	private final Integer numberOfMembers;

	private final List<TreeRelation> relations;

	@Indexed
	private final String collectionName;

	public LdesFragmentEntityV3(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
			Boolean immutable,
			Boolean softDeleted, String parentId, LocalDateTime immutableTimestamp, Integer numberOfMembers,
			List<TreeRelation> relations, String collectionName) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.softDeleted = softDeleted;
		this.parentId = parentId;
		this.immutableTimestamp = immutableTimestamp;
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

	public Boolean getSoftDeleted() {
		return softDeleted;
	}

	public String getParentId() {
		return parentId;
	}

	public LocalDateTime getImmutableTimestamp() {
		return immutableTimestamp;
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
