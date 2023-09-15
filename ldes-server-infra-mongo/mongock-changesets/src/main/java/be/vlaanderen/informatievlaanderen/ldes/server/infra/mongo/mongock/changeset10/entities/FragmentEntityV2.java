package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.valueobjects.TreeRelationV2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("fragmentation_fragment")
public class FragmentEntityV2 {
	@Id
	private String id;
	@Indexed
	private Boolean root;
	@Indexed
	private String viewName;
	private List<FragmentPair> fragmentPairs;
	@Indexed
	private Boolean immutable;
	@Indexed
	private String parentId;
	private Integer numberOfMembers;
	private List<TreeRelationV2> relations;
	@Indexed
	private String collectionName;
	private LocalDateTime deleteTime;

	public FragmentEntityV2(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
			Boolean immutable, String parentId, Integer numberOfMembers,
			List<TreeRelationV2> relations, String collectionName, LocalDateTime deleteTime) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.parentId = parentId;
		this.numberOfMembers = numberOfMembers;
		this.relations = relations;
		this.collectionName = collectionName;
		this.deleteTime = deleteTime;
	}

	public FragmentEntityV2() {
	}

	public String getId() {
		return id;
	}

	public Boolean isImmutable() {
		return immutable;
	}

	public String getViewName() {
		return viewName;
	}

	public List<TreeRelationV2> getRelations() {
		return relations;
	}

	public Boolean getRoot() {
		return root;
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

	public String getCollectionName() {
		return collectionName;
	}

	public LocalDateTime getDeleteTime() {
		return deleteTime;
	}
}
