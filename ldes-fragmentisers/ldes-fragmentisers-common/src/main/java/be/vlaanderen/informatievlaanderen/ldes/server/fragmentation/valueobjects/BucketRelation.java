package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;

public record BucketRelation(String treeRelationType, String treeValue, String treeValueType, String treePath) {

	public static BucketRelation generic() {
		return new BucketRelation(RdfConstants.GENERIC_TREE_RELATION, "", "", "");
	}

}
