package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;

public record BucketRelation(
		Bucket fromBucket,
		Bucket toBucket,
		String treeRelationType,
		String treeValue,
		String treeValueType,
		String treePath
) {
	public static BucketRelation createGenericRelation(Bucket fromBucket, Bucket toBucket) {
		return new BucketRelation(fromBucket, toBucket, RdfConstants.GENERIC_TREE_RELATION, "", "", "");
	}
}
