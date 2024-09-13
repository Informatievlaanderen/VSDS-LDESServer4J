package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;

public class ChildBucket extends Bucket {
	private final BucketRelation relation;

	public ChildBucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, BucketRelation relation) {
		super(bucketId, bucketDescriptor, viewName);
		this.relation = relation;
	}

	public ChildBucket(BucketDescriptor bucketDescriptor, ViewName viewName, BucketRelation relation) {
		super(bucketDescriptor, viewName);
		this.relation = relation;
	}

	public BucketRelation getRelation() {
		return relation;
	}
}
