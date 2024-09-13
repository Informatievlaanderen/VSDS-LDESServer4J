package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;

public final class ExtendedBucketRelation extends BucketRelation {
	private final Bucket fromBucket;
	private final Bucket toBucket;

	public ExtendedBucketRelation(
			Bucket fromBucket,
			Bucket toBucket,
			String treeRelationType,
			String treeValue,
			String treeValueType,
			String treePath
	) {
		super(treeRelationType, treeValue, treeValueType, treePath);
		this.fromBucket = fromBucket;
		this.toBucket = toBucket;
	}

	public static ExtendedBucketRelation createGenericRelation(Bucket fromBucket, Bucket toBucket) {
		return new ExtendedBucketRelation(fromBucket, toBucket, RdfConstants.GENERIC_TREE_RELATION, "", "", "");
	}

	public Bucket fromBucket() {
		return fromBucket;
	}

	public Bucket toBucket() {
		return toBucket;
	}

}
