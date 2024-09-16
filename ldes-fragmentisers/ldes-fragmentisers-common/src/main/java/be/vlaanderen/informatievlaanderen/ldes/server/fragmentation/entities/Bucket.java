package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;


import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.DuplicateFragmentPairException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Bucket {
	private final long bucketId;
	private final BucketDescriptor bucketDescriptor;
	private final ViewName viewName;
	private final List<ChildBucket> children;
	private final List<BucketisedMember> members;

	public Bucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, List<ChildBucket> children, List<BucketisedMember> members) {
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

	public void addChildBucket(ChildBucket childBucket) {
		children.add(childBucket);
	}

	public ChildBucket withRelation(TreeRelation relationDefinition) {
		return new ChildBucket(
				bucketId,
				bucketDescriptor,
				viewName,
				children,
				members,
				relationDefinition
		);
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
		members.add(new BucketisedMember(bucketId, memberId));
	}

	public List<BucketisedMember> getMembers() {
		return List.copyOf(members);
	}

	public List<ChildBucket> getChildren() {
		return List.copyOf(children);
	}

	public List<Bucket> getBucketTree() {
		return getDescendants(this);
	}

	public List<BucketisedMember> getAllMembers() {
		return getDescendants(this).stream()
				.flatMap(bucket -> bucket.getMembers().stream())
				.toList();
	}

	private static List<Bucket> getDescendants(Bucket bucket) {
		List<Bucket> descendants = new ArrayList<>();
		descendants.add(bucket);
		bucket.getChildren().stream()
				.flatMap(child -> getDescendants(child).stream())
				.forEach(descendants::add);
		return descendants;
	}

	public List<BucketRelation> getAllRelations() {
		return getAllRelations(this);
	}

	private static List<BucketRelation> getAllRelations(Bucket bucket) {
		List<BucketRelation> relations = new ArrayList<>();
		bucket.getChildren().stream()
				.map(child -> new BucketRelation(bucket, child, child.getRelation()))
				.forEach(bucketrelation -> {
					relations.addAll(getAllRelations(bucketrelation.toBucket()));
					relations.add(bucketrelation);
				});
		return relations;
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

	@Override
	public String toString() {
		return createPartialUrl();
	}

	public Optional<String> getValueForKey(String key) {
		return bucketDescriptor.getValueForKey(key);
	}
}
