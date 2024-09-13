package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;

import java.util.Objects;

public class BucketRelation {
	private final String treeRelationType;
	private final String treeValue;
	private final String treeValueType;
	private final String treePath;

	public BucketRelation(String treeRelationType,
	                      String treeValue,
	                      String treeValueType,
	                      String treePath) {
		this.treeRelationType = treeRelationType;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.treePath = treePath;
	}

	public static BucketRelation generic() {
		return new BucketRelation(RdfConstants.GENERIC_TREE_RELATION, "", "", "");
	}

	public String treeRelationType() {
		return treeRelationType;
	}

	public String treeValue() {
		return treeValue;
	}

	public String treeValueType() {
		return treeValueType;
	}

	public String treePath() {
		return treePath;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (BucketRelation) obj;
		return Objects.equals(this.treeRelationType, that.treeRelationType) &&
				Objects.equals(this.treeValue, that.treeValue) &&
				Objects.equals(this.treeValueType, that.treeValueType) &&
				Objects.equals(this.treePath, that.treePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(treeRelationType, treeValue, treeValueType, treePath);
	}

	@Override
	public String toString() {
		return "BucketRelationDefinition[" +
				"treeRelationType=" + treeRelationType + ", " +
				"treeValue=" + treeValue + ", " +
				"treeValueType=" + treeValueType + ", " +
				"treePath=" + treePath + ']';
	}

}
