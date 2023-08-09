package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

import java.util.List;
import java.util.Objects;

public class TreeNodeDto {
	private final TreeNode treeNode;
	private final String fragmentId;
	private final List<String> treeNodeIdsInRelations;
	private final List<String> memberIds;
	private final boolean immutable;
	private final boolean isView;
	private final List<Member> members;
	private final String collectionName;

	public TreeNodeDto(TreeNode treeNode, String fragmentId, List<String> treeNodeIdsInRelations,
			List<String> memberIds, boolean immutable,
			boolean isView,
			List<Member> members, String collectionName) {
		this.treeNode = treeNode;
		this.fragmentId = fragmentId;
		this.treeNodeIdsInRelations = treeNodeIdsInRelations;
		this.memberIds = memberIds;
		this.immutable = immutable;
		this.isView = isView;
		this.members = members;
		this.collectionName = collectionName;
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public List<String> getTreeNodeIdsInRelations() {
		return treeNodeIdsInRelations;
	}

	public List<Member> getMembers() {
		return members;
	}

	public boolean isView() {
		return isView;
	}

	public TreeNode getTreeNode() {
		return treeNode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TreeNodeDto that = (TreeNodeDto) o;
		return Objects.equals(fragmentId, that.fragmentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fragmentId);
	}

	public String getCollectionName() {
		return collectionName;
	}

	public List<String> getMemberIds() {
		return memberIds;
	}
}
