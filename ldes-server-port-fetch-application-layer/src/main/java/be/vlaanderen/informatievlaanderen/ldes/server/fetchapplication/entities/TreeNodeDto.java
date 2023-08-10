package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNode;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Objects;

public class TreeNodeDto {
	private final TreeNode treeNode;
	private final String fragmentId;
	private final List<String> treeNodeIdsInRelations;
	private final List<String> memberIds;
	private final boolean immutable;
	private final boolean isView;
	private final String collectionName;

	public TreeNodeDto(TreeNode treeNode, String fragmentId, List<String> treeNodeIdsInRelations,
			List<String> memberIds, boolean immutable,
			boolean isView,
			String collectionName) {
		this.treeNode = treeNode;
		this.fragmentId = fragmentId;
		this.treeNodeIdsInRelations = treeNodeIdsInRelations;
		this.memberIds = memberIds;
		this.immutable = immutable;
		this.isView = isView;
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

	public boolean isView() {
		return isView;
	}

	public Model getModel() {
		return treeNode.getModel();
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
