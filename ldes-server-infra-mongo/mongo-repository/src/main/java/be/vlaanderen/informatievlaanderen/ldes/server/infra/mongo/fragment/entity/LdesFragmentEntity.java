package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ldesfragment")
public class LdesFragmentEntity {
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

	public LdesFragmentEntity(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
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

	public LdesFragment toLdesFragment() {
		int effectiveNumberOfMembers = numberOfMembers == null ? 0 : numberOfMembers;
		return new LdesFragment(new LdesFragmentIdentifier(ViewName.fromString(viewName), fragmentPairs), immutable,
				effectiveNumberOfMembers,
				relations);
	}

	public String getViewName() {
		return viewName;
	}

	public List<FragmentPair> getFragmentPairs() {
		return fragmentPairs;
	}

	public static LdesFragmentEntity fromLdesFragment(LdesFragment ldesFragment) {
		return new LdesFragmentEntity(ldesFragment.getFragmentIdString(),
				ldesFragment.isRoot(),
				ldesFragment.getFragmentId().getViewName().asString(),
				ldesFragment.getFragmentPairs(),
				ldesFragment.isImmutable(),
				ldesFragment.getParentIdAsString(),
				ldesFragment.getNumberOfMembers(),
				ldesFragment.getRelations(),
				ldesFragment.getFragmentId().getViewName().getCollectionName());
	}

	public Boolean getRoot() {
		return root;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}
}
