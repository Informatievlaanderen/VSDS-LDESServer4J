package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;

public record PageRelation(
		Page fromPage,
		Page toPage,
		String treeRelationType,
		String treeValue,
		String treeValueType,
		String treePath
) {

	public static PageRelation createGenericRelation(Page fromPage, Page toPage) {
		return new PageRelation(fromPage, toPage, RdfConstants.GENERIC_TREE_RELATION, null, null, null);
	}

	public static PageRelation fromBucketRelation(BucketRelation bucketRelation) {
		return new PageRelation(
				Page.fromBucket(bucketRelation.fromBucket()),
				Page.fromBucket(bucketRelation.toBucket()),
				bucketRelation.treeRelationType(),
				bucketRelation.treeValue(),
				bucketRelation.treeValueType(),
				bucketRelation.treePath()
		);
	}
}
