package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelationDefinition;

public class ChildBucket extends Bucket {
	private final BucketRelationDefinition relation;

	public ChildBucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, BucketRelationDefinition relation) {
		super(bucketId, bucketDescriptor, viewName);
		this.relation = relation;
	}

	public ChildBucket(BucketDescriptor bucketDescriptor, ViewName viewName, BucketRelationDefinition relation) {
		super(bucketDescriptor, viewName);
		this.relation = relation;
	}

	public BucketRelationDefinition getRelation() {
		return relation;
	}
}
