package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11.entities.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11.entities.valueobjects.TreeRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("fragmentation_fragment")
public class FragmentEntityV3 {
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
	private Integer nrOfMembersAdded;
	private List<TreeRelation> relations;
	@Indexed
	private String collectionName;
	private LocalDateTime deleteTime;

	public FragmentEntityV3(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
			Boolean immutable, String parentId, Integer nrOfMembersAdded,
			List<TreeRelation> relations, String collectionName, LocalDateTime deleteTime) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.parentId = parentId;
		this.nrOfMembersAdded = nrOfMembersAdded;
		this.relations = relations;
		this.collectionName = collectionName;
		this.deleteTime = deleteTime;
	}

	public FragmentEntityV3() {
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

	public List<TreeRelation> getRelations() {
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

	public Integer getNrOfMembersAdded() {
		return nrOfMembersAdded;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public LocalDateTime getDeleteTime() {
		return deleteTime;
	}
}
