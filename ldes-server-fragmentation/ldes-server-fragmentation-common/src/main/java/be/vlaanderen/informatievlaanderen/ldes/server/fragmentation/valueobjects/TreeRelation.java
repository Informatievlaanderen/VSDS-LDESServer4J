package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;

public record TreeRelation(String treeRelationType, String treeValue, String treeValueType, String treePath) {

	public static TreeRelation generic() {
		return new TreeRelation(RdfConstants.GENERIC_TREE_RELATION, "", "", "");
	}

}
