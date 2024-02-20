package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.jetbrains.annotations.Nullable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TreeNode {
	private final String fragmentId;
	private final boolean immutable;
	private final boolean isView;
	private final List<TreeRelation> relations;
	private final List<Member> members;
	private final String collectionName;
	private long numberOfMembersInView;
	private final LocalDateTime nextUpdateTs;

	public TreeNode(String encodedFragmentId, boolean immutable, boolean isView,
					List<TreeRelation> relations,
					List<Member> members, String collectionName, LocalDateTime nextUpdateTs) {
		this.fragmentId = encodedFragmentId;
		this.immutable = immutable;
		this.isView = isView;
		this.relations = relations;
		this.members = members;
		this.collectionName = collectionName;
		this.numberOfMembersInView = 0L;
		this.nextUpdateTs = nextUpdateTs;
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public List<Member> getMembers() {
		return members;
	}

	public boolean isView() {
		return isView;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TreeNode that = (TreeNode) o;
		return Objects.equals(fragmentId, that.fragmentId);
	}

	@Nullable
	public LocalDateTime getNextUpdateTs() {
		return nextUpdateTs;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fragmentId);
	}

	public String getCollectionName() {
		return collectionName;
	}

	public long getNumberOfMembersInView() {
		return numberOfMembersInView;
	}

	public void setNumberOfMembersInView(long numberOfMembersInView) {
		this.numberOfMembersInView = numberOfMembersInView;
	}

}
