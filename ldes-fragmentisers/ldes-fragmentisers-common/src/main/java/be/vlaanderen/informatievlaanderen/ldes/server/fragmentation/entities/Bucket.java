package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;


import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.DuplicateFragmentPairException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;

import java.util.*;
import java.util.stream.Stream;

public class Bucket {
	private long bucketId;
	private final BucketDescriptor bucketDescriptor;
	private final ViewName viewName;
	private final List<ChildBucket> children;
	private final List<Long> members;

	public Bucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, List<ChildBucket> children, List<Long> members) {
		this.bucketId = bucketId;
		this.bucketDescriptor = bucketDescriptor;
		this.viewName = viewName;
		this.children = children;
		this.members = members;
	}

	public Bucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName) {
		this(bucketId, bucketDescriptor, viewName, new ArrayList<>(), new ArrayList<>());
	}

	public Bucket(BucketDescriptor bucketDescriptor, ViewName viewName) {
		this(0, bucketDescriptor, viewName);
	}

	public static Bucket createRootBucketForView(ViewName viewName) {
		return new Bucket(BucketDescriptor.empty(), viewName);
	}

	public long getBucketId() {
		return bucketId;
	}

	public void setBucketId(long bucketId) {
		this.bucketId = bucketId;
	}

	public ViewName getViewName() {
		return viewName;
	}

	public List<BucketDescriptorPair> getBucketDescriptorPairs() {
		return bucketDescriptor.getDescriptorPairs();
	}

	public BucketDescriptor getBucketDescriptor() {
		return bucketDescriptor;
	}

	public String getBucketDescriptorAsString() {
		return bucketDescriptor.asDecodedString();
	}

	public ChildBucket addChildBucket(ChildBucket childBucket) {
		final int index = children.indexOf(childBucket);
		if(index == -1) {
			children.add(childBucket);
			return childBucket;
		}
		ChildBucket existingChildBucket = children.get(index);
		existingChildBucket.addRelations(childBucket.getRelations());
		return existingChildBucket;
	}


	public ChildBucket withRelation(TreeRelation... relationDefinition) {
		return new ChildBucket(bucketId, bucketDescriptor, viewName, children, members, Set.of(relationDefinition));
	}

	public ChildBucket withGenericRelation() {
		return withRelation(TreeRelation.generic());
	}

	public Bucket createChild(BucketDescriptorPair descriptorPair) {
		return new Bucket(createChildDescriptor(descriptorPair), viewName);
	}

	public BucketDescriptor createChildDescriptor(BucketDescriptorPair descriptorPair) {
		List<BucketDescriptorPair> childFragmentPairs = new ArrayList<>(this.bucketDescriptor.getDescriptorPairs());
		if (hasChildWithSameDescriptorKey(descriptorPair, childFragmentPairs)) {
			throw new DuplicateFragmentPairException(bucketDescriptor.asDecodedString(), descriptorPair.key());
		}
		childFragmentPairs.add(descriptorPair);
		return new BucketDescriptor(childFragmentPairs);
	}

	public void addMember(long memberId) {
		members.add(memberId);
	}

	public List<Long> getMembers() {
		return List.copyOf(members);
	}

	public List<BucketisedMember> getBucketisedMembers() {
		return members.stream().map(memberId -> new BucketisedMember(bucketId, memberId)).toList();
	}

	public List<BucketisedMember> getAllBucketisedMembers() {
		return Stream.concat(
				members.stream().map(memberId -> new BucketisedMember(bucketId, memberId)),
				children.stream().flatMap(child -> child.getAllBucketisedMembers().stream())
		).toList();
	}

	public List<ChildBucket> getChildren() {
		return List.copyOf(children);
	}

	List<Bucket> getAllDescendants() {
		return Stream.concat(
						children.stream(),
						children.stream().flatMap(child -> child.getAllDescendants().stream())
				)
				.toList();
	}

	public List<Bucket> getBucketTree() {
		final List<Bucket> bucketTree = new ArrayList<>(List.of(this));
		getAllDescendants().stream().distinct().forEach(bucketTree::add);
		return bucketTree;
	}

	public List<BucketRelation> getChildRelations() {
		return children.stream()
				.flatMap(child -> child.getRelations().stream()
						.map(relation -> new BucketRelation(createPartialUrl(), child.createPartialUrl(), relation))
				)
				.toList();
	}

	public String createPartialUrl() {
		return "/" + viewName.asString() + (bucketDescriptor.isEmpty() ? "" : "?" + bucketDescriptor.asDecodedString());
	}

	private static boolean hasChildWithSameDescriptorKey(BucketDescriptorPair descriptorPair, List<BucketDescriptorPair> childFragmentPairs) {
		return childFragmentPairs
				.stream()
				.map(BucketDescriptorPair::key)
				.anyMatch(key -> key.equals(descriptorPair.key()));
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Bucket bucket)) return false;

		return Objects.equals(bucketDescriptor, bucket.bucketDescriptor) && Objects.equals(viewName, bucket.viewName);
	}

	@Override
	public int hashCode() {
		int result = Objects.hashCode(bucketDescriptor);
		result = 31 * result + Objects.hashCode(viewName);
		return result;
	}

	public Optional<String> getValueForKey(String key) {
		return bucketDescriptor.getValueForKey(key);
	}
}
