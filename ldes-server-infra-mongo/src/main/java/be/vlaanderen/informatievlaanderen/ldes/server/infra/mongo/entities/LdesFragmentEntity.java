package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("ldesfragment")
@CompoundIndex(name = "index_view_fragmentPairs", def = "{'viewName' : 1, 'fragmentPairs': 1}")
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
	private final List<TreeRelation> relations;
	private final int numberOfMembers;

	public LdesFragmentEntity(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
							  Boolean immutable,
							  Boolean softDeleted, String parentId, LocalDateTime immutableTimestamp, List<TreeRelation> relations, int numberOfMembers) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.softDeleted = softDeleted;
		this.parentId = parentId;
		this.immutableTimestamp = immutableTimestamp;
		this.relations = relations;
		this.numberOfMembers = numberOfMembers;
	}

	public String getId() {
		return id;
	}

	public FragmentInfo getFragmentInfo() {
		return new FragmentInfo(viewName, fragmentPairs, immutable, immutableTimestamp, softDeleted, numberOfMembers);
	}

	public Boolean isImmutable() {
		return immutable;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public LdesFragment toLdesFragment() {
		LdesFragment ldesFragment = new LdesFragment(getFragmentInfo());
		relations.forEach(ldesFragment::addRelation);
		return ldesFragment;
	}

	public static LdesFragmentEntity fromLdesFragment(LdesFragment ldesFragment) {
		return new LdesFragmentEntity(ldesFragment.getFragmentId(),
				ldesFragment.getFragmentInfo().getFragmentPairs().isEmpty(),
				ldesFragment.getFragmentInfo().getViewName(),
				ldesFragment.getFragmentInfo().getFragmentPairs(), ldesFragment.getFragmentInfo().getImmutable(),
				ldesFragment.getFragmentInfo().getSoftDeleted(), ldesFragment.getFragmentInfo().getParentId(), ldesFragment.getFragmentInfo().getImmutableTimestamp(),
				ldesFragment.getRelations(),
				 ldesFragment.getFragmentInfo().getNumberOfMembers());
	}
}
