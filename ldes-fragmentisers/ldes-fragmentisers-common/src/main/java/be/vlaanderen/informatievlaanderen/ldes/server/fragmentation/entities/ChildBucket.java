package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;

import java.util.List;

public class ChildBucket extends Bucket {
	private final TreeRelation relation;

	public ChildBucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, List<ChildBucket> children, List<BucketisedMember> members, TreeRelation relation) {
		super(bucketId, bucketDescriptor, viewName, children, members);
		this.relation = relation;
	}

	public TreeRelation getRelation() {
		return relation;
	}
}
