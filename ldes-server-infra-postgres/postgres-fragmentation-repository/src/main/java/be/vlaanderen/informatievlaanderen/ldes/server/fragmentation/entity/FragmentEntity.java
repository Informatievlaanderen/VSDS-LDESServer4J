package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.converter.MapToStringConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"java:S1068", "java:S107"})
@Entity
@Table(name = "fragmentation_fragment", indexes = {
		@Index(columnList = "viewName"),
		@Index(columnList = "collectionName"),
		@Index(columnList = "deleteTime"),
		@Index(name = "root_of_view", columnList = "root, viewName"),
		@Index(name = "immutable_with_parent", columnList = "immutable, parentId")
})
public class FragmentEntity {
	@Id
	private String id;
	private Boolean root;
	private String viewName;
	@Convert(converter = MapToStringConverter.class)
	private Map<String, String> fragmentPairs;
	private Boolean immutable;
	private String parentId;
	private Integer nrOfMembersAdded;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "fragmentation_fragment_relations",
			joinColumns = @JoinColumn(name = "fragment_id")
	)
	private List<TreeRelationEntity> relations;
	private String collectionName;
	private LocalDateTime deleteTime;
	private LocalDateTime nextUpdateTs;

	public FragmentEntity(String id, Boolean root, String viewName, List<FragmentPair> fragmentPairs,
	                      Boolean immutable, String parentId, Integer nrOfMembersAdded,
	                      List<TreeRelation> relations, String collectionName, LocalDateTime deleteTime, LocalDateTime nextUpdateTs) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs.stream()
				.collect(Collectors.toMap(FragmentPair::fragmentKey, FragmentPair::fragmentValue));
		this.immutable = immutable;
		this.parentId = parentId;
		this.nrOfMembersAdded = nrOfMembersAdded;
		this.relations = relations.stream().map(TreeRelationEntity::toEntity).toList();
		this.collectionName = collectionName;
		this.deleteTime = deleteTime;
		this.nextUpdateTs = nextUpdateTs;
	}

	protected FragmentEntity() {
	}

	public String getId() {
		return id;
	}

	public Boolean isImmutable() {
		return immutable;
	}

	public Fragment toLdesFragment() {
		int effectiveNrOfMembersAdded = nrOfMembersAdded == null ? 0 : nrOfMembersAdded;
		final var ldesFragmentIdentifier = new LdesFragmentIdentifier(ViewName.fromString(viewName),
				fragmentPairs.entrySet()
						.stream()
						.map(FragmentPair::fromMapEntry)
						.toList());
		var relationList = new ArrayList<>(relations.stream().map(TreeRelationEntity::toTreeRelation).toList());
		var fragment =
				new Fragment(ldesFragmentIdentifier, immutable, effectiveNrOfMembersAdded, relationList, deleteTime);
		fragment.setNextUpdateTs(nextUpdateTs);
		return fragment;
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
				fragment.getFragmentId().getViewName().getCollectionName(),
				fragment.getDeleteTime(),
				fragment.getNextUpdateTs());
	}

	public List<TreeRelationEntity> getRelations() {
		return relations;
	}

	public void removeRelation(TreeRelationEntity treeRelation) {
		relations.remove(treeRelation);
	}
}
