package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings({"java:S1068", "java:S107"})
@Document("fragmentation_fragment")
@CompoundIndex(name = "root_of_view", def = "{'root' : 1, 'viewName': 1}")
@CompoundIndex(name = "immutable_with_parent", def = "{'immutable' : 1, 'parentId': 1}")
public class FragmentEntity {
	@Id
	private String id;
	private Boolean root;
	@Indexed
	private String viewName;
	private List<FragmentPair> fragmentPairs;
	private Boolean immutable;
	private String parentId;
	private Integer nrOfMembersAdded;
	private List<TreeRelation> relations;
	@Indexed
	private String collectionName;
	@Indexed
	private LocalDateTime deleteTime;

	public FragmentEntity(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
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

	public FragmentEntity() {
	}

	public String getId() {
		return id;
	}

	public Boolean isImmutable() {
		return immutable;
	}

	public Fragment toLdesFragment() {
		int effectiveNrOfMembersAdded = nrOfMembersAdded == null ? 0 : nrOfMembersAdded;
		return new Fragment(new LdesFragmentIdentifier(ViewName.fromString(viewName), fragmentPairs), immutable,
				effectiveNrOfMembersAdded,
				relations, deleteTime);
	}

	public String getViewName() {
		return viewName;
	}

	public static FragmentEntity fromLdesFragment(Fragment fragment) {
		return new FragmentEntity(fragment.getFragmentIdString(),
				fragment.isRoot(),
				fragment.getFragmentId().getViewName().asString(),
				fragment.getFragmentPairs(),
				fragment.isImmutable(),
				fragment.getParentIdAsString(),
				fragment.getNrOfMembersAdded(),
				fragment.getRelations(),
				fragment.getFragmentId().getViewName().getCollectionName(), fragment.getDeleteTime());
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public void removeRelation(TreeRelation treeRelation) {
		relations.remove(treeRelation);
	}
}
