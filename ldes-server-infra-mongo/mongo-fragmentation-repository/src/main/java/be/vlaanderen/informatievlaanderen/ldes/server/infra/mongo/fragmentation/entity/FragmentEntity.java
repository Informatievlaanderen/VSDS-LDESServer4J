package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("fragmentation_fragment")
public class FragmentEntity {
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

	public FragmentEntity(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
			Boolean immutable, String parentId, Integer numberOfMembers,
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

	public Boolean isImmutable() {
		return immutable;
	}

	public Fragment toLdesFragment() {
		int effectiveNumberOfMembers = numberOfMembers == null ? 0 : numberOfMembers;
		return new Fragment(new LdesFragmentIdentifier(ViewName.fromString(viewName), fragmentPairs), immutable,
				effectiveNumberOfMembers,
				relations);
	}

	public String getViewName() {
		return viewName;
	}

	public List<FragmentPair> getFragmentPairs() {
		return fragmentPairs;
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
				fragment.getFragmentId().getViewName().getCollectionName());
	}

	public Boolean getRoot() {
		return root;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}
}
