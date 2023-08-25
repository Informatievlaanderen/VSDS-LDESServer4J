package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("fragmentation_fragment")
public class FragmentEntity {
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
	private List<TreeRelation> relations;
	@Indexed
	private String collectionName;
	private LocalDateTime deleteTime;

	public FragmentEntity(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
			Boolean immutable, String parentId, Integer numberOfMembers,
			List<TreeRelation> relations, String collectionName, LocalDateTime localDateTime) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.parentId = parentId;
		this.numberOfMembers = numberOfMembers;
		this.relations = relations;
		this.collectionName = collectionName;
		this.deleteTime = localDateTime;
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
		int effectiveNumberOfMembers = numberOfMembers == null ? 0 : numberOfMembers;
		return new Fragment(new LdesFragmentIdentifier(ViewName.fromString(viewName), fragmentPairs), immutable,
				effectiveNumberOfMembers,
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
				fragment.getNumberOfMembers(),
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
