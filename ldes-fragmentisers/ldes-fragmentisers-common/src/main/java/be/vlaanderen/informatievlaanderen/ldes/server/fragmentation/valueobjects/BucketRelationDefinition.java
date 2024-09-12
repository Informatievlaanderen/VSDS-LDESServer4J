package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;

public record BucketRelationDefinition(String treeRelationType,
                                       String treeValue,
                                       String treeValueType,
                                       String treePath) {
	public static BucketRelationDefinition generic() {
		return new BucketRelationDefinition(RdfConstants.GENERIC_TREE_RELATION, "", "", "");
	}
}
