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
	private long assignedMemberId;

	public Bucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, List<ChildBucket> children, List<Long> members) {
		this.bucketId = bucketId;
		this.bucketDescriptor = bucketDescriptor;
		this.viewName = viewName;
		this.children = new ArrayList<>(children);
		this.members = new ArrayList<>(members);
	}

	public Bucket(BucketDescriptor bucketDescriptor, ViewName viewName) {
		this(0, bucketDescriptor, viewName, new ArrayList<>(), new ArrayList<>());
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

	public BucketDescriptor getBucketDescriptor() {
		return bucketDescriptor;
	}

	public String getBucketDescriptorAsString() {
		return bucketDescriptor.asDecodedString();
	}

	public ChildBucket addChildBucket(ChildBucket childBucket) {
		final int index = children.indexOf(childBucket);
		if (index == -1) {
			children.add(childBucket);
			return childBucket;
		}
		ChildBucket existingChildBucket = children.get(index);
		existingChildBucket.addRelations(childBucket.getRelations());
		return existingChildBucket;
	}


	public ChildBucket withRelations(TreeRelation... relationDefinition) {
		return new ChildBucket(bucketId, bucketDescriptor, viewName, children, members, Set.of(relationDefinition));
	}

	public ChildBucket withGenericRelation() {
		return withRelations(TreeRelation.generic());
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

	public void assignMember(long memberId) {
		assignedMemberId = memberId;
		members.add(memberId);
	}

	public Optional<BucketisedMember> getMember() {
		return Optional.of(assignedMemberId)
				.filter(member -> member != 0)
				.map(member -> new BucketisedMember(bucketId, member));
	}

	public List<BucketisedMember> getBucketisedMembers() {
		return members.stream().map(memberId -> new BucketisedMember(bucketId, memberId)).toList();
	}

	public List<ChildBucket> getChildren() {
		return List.copyOf(children);
	}

	public List<Bucket> getAllDescendants() {
		return children.stream()
				.flatMap(child -> Stream.concat(Stream.of(child), child.getAllDescendants().stream()))
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

	public void merge(Bucket bucket) {
		if (this.equals(bucket)) {
			this.children.addAll(bucket.children);
			this.members.addAll(bucket.members);
		}
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
