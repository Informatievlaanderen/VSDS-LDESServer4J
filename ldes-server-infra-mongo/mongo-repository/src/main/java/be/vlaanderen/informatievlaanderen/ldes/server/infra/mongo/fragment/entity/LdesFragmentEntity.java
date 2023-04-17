package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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
	private final Boolean softDeleted;

	@Indexed
	private final String parentId;

	private final LocalDateTime immutableTimestamp;

	private final Integer numberOfMembers;

	private final List<TreeRelation> relations;

	@Indexed
	private final String collectionName;

	public LdesFragmentEntity(String id, Boolean root, ViewName viewName, List<FragmentPair> fragmentPairs,
			Boolean immutable,
			Boolean softDeleted, String parentId, LocalDateTime immutableTimestamp, Integer numberOfMembers,
			List<TreeRelation> relations) {
		this.id = id;
		this.root = root;
		this.viewName = viewName.getFullName();
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.softDeleted = softDeleted;
		this.parentId = parentId;
		this.immutableTimestamp = immutableTimestamp;
		this.numberOfMembers = numberOfMembers;
		this.relations = relations;
		this.collectionName = viewName.getCollectionName();
	}

	public String getId() {
		return id;
	}

	public Boolean isImmutable() {
		return immutable;
	}

	public LdesFragment toLdesFragment() {
		int effectiveNumberOfMembers = numberOfMembers == null ? 0 : numberOfMembers;
		return new LdesFragment(new ViewName(collectionName, viewName), fragmentPairs, immutable, immutableTimestamp,
				softDeleted,
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
		return new LdesFragmentEntity(ldesFragment.getFragmentId(),
				ldesFragment.getFragmentPairs().isEmpty(),
				ldesFragment.getViewName(),
				ldesFragment.getFragmentPairs(), ldesFragment.isImmutable(),
				ldesFragment.isSoftDeleted(), ldesFragment.getParentId(), ldesFragment.getImmutableTimestamp(),
				ldesFragment.getNumberOfMembers(), ldesFragment.getRelations());
	}

	public Boolean getRoot() {
		return root;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}
}
