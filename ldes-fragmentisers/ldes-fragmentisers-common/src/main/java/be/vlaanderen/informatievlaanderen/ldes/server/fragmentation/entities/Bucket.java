package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;


import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.DuplicateFragmentPairException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;

import java.util.ArrayList;
import java.util.List;

public class Bucket {

	private final BucketDescriptor bucketDescriptor;
	private final ViewName viewName;
	private final int memberCount;

	public Bucket(BucketDescriptor bucketDescriptor, ViewName viewName, int memberCount) {
		this.bucketDescriptor = bucketDescriptor;
		this.viewName = viewName;
		this.memberCount = memberCount;
	}


	public Bucket(BucketDescriptor bucketDescriptor, ViewName viewName) {
		this(bucketDescriptor, viewName, 0);
	}

	public ViewName getViewName() {
		return viewName;
	}

	public List<BucketDescriptorPair> getBucketDescriptorPairs() {
		return bucketDescriptor.getDescriptorPairs();
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
		return new Bucket(new BucketDescriptor(childFragmentPairs), viewName, 0);
	}

	private static boolean hasChildWithSameDescriptorKey(BucketDescriptorPair descriptorPair, List<BucketDescriptorPair> childFragmentPairs) {
		return childFragmentPairs
				.stream()
				.map(BucketDescriptorPair::key)
				.anyMatch(key -> key.equals(descriptorPair.key()));
	}
}
