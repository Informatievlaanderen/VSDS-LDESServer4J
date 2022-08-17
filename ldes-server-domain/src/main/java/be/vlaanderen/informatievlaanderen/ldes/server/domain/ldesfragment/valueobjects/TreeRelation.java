package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import java.util.Objects;

public class TreeRelation {
	private final String treePath;
	private final String treeValue;
	private final String treeValueType;
	private final String treeNode;
	private final String relation;

	public TreeRelation(String treePath, String treeNode, String treeValue, String treeValueType, String relation) {
		this.treePath = treePath;
		this.treeNode = treeNode;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.relation = relation;
	}

	public String getTreePath() {
		return treePath;
	}

	public String getTreeValue() {
		return treeValue;
	}

	public String getTreeNode() {
		return treeNode;
	}

	public String getRelation() {
		return relation;
	}

	public String getTreeValueType() {
		return treeValueType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TreeRelation that = (TreeRelation) o;
		return Objects.equals(treePath, that.treePath) && Objects.equals(treeValue, that.treeValue)
				&& Objects.equals(treeNode, that.treeNode) && Objects.equals(relation, that.relation)
				&& Objects.equals(treeValueType, that.treeValueType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(treePath, treeValue, treeNode, relation);
	}
}
