package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;


import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.DuplicateFragmentPairException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Bucket {
	private final long bucketId;
	private final BucketDescriptor bucketDescriptor;
	private final ViewName viewName;

	public Bucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName) {
		this.bucketId = bucketId;
		this.bucketDescriptor = bucketDescriptor;
		this.viewName = viewName;
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

	public Bucket createChild(BucketDescriptorPair descriptorPair) {
		List<BucketDescriptorPair> childFragmentPairs = new ArrayList<>(this.bucketDescriptor.getDescriptorPairs());
		if (hasChildWithSameDescriptorKey(descriptorPair, childFragmentPairs)) {
			throw new DuplicateFragmentPairException(bucketDescriptor.asDecodedString(), descriptorPair.key());
		}
		childFragmentPairs.add(descriptorPair);
		return new Bucket(new BucketDescriptor(childFragmentPairs), viewName);
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
