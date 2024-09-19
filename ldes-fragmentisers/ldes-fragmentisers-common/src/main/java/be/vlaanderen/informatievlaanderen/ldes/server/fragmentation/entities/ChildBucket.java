package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChildBucket extends Bucket {
	private final Set<TreeRelation> relations;

	public ChildBucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, List<ChildBucket> children, List<Long> members, Set<TreeRelation> relations) {
		super(bucketId, bucketDescriptor, viewName, children, members);
		this.relations = new HashSet<>(relations);
	}

	public Set<TreeRelation> getRelations() {
		return relations;
	}

	public void addRelations(Set<TreeRelation> relations) {
		this.relations.addAll(relations);
	}
}
