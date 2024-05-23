package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity;


import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import jakarta.persistence.Embeddable;

@Embeddable
public class TreeRelationEntity {
	private String treePath;
	private String treeNode;
	private String treeValue;
	private String treeValueType;
	private String relation;

	protected TreeRelationEntity() {
	}

	public TreeRelationEntity(String treePath, String treeNode, String treeValue, String treeValueType,
	                          String relation) {
		this.treePath = treePath;
		this.treeNode = treeNode;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.relation = relation;
	}

	public TreeRelation toTreeRelation() {
		return new TreeRelation(treePath, LdesFragmentIdentifier.fromFragmentId(treeNode), treeValue,
				treeValueType, relation);
	}

	public static TreeRelationEntity toEntity(TreeRelation treeRelation) {
		return new TreeRelationEntity(treeRelation.treePath(), treeRelation.treeNode().asDecodedFragmentId(),
				treeRelation.treeValue(), treeRelation.treeValueType(), treeRelation.relation());
	}

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public String getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(String treeNode) {
		this.treeNode = treeNode;
	}

	public String getTreeValue() {
		return treeValue;
	}

	public void setTreeValue(String treeValue) {
		this.treeValue = treeValue;
	}

	public String getTreeValueType() {
		return treeValueType;
	}

	public void setTreeValueType(String treeValueType) {
		this.treeValueType = treeValueType;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}
}
