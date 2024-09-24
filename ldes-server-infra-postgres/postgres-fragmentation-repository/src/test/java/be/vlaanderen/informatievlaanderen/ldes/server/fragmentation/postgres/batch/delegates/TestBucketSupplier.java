package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;

import java.util.List;
import java.util.function.Supplier;

public class TestBucketSupplier implements Supplier<Bucket> {
	private final ViewName viewName;
	private final BucketDescriptorPair[] bucketDescriptorPairs;
	private final long memberId;
	private final boolean idGenerationEnabled;
	private int currentId = 0;

	public TestBucketSupplier(ViewName viewName, BucketDescriptorPair[] bucketDescriptorPairs, long memberId, boolean idGenerationEnabled) {
		this.viewName = viewName;
		this.bucketDescriptorPairs = bucketDescriptorPairs;
		this.memberId = memberId;
		this.idGenerationEnabled = idGenerationEnabled;
	}

	public TestBucketSupplier(ViewName viewName, BucketDescriptorPair[] bucketDescriptorPairs, long memberId) {
		this(viewName, bucketDescriptorPairs, memberId, false);
	}

	@Override
	public Bucket get() {
		Bucket rootBucket = Bucket.createRootBucketForView(viewName);
		assignBucketIdIfNecessary(rootBucket);
		Bucket nextParent = rootBucket;
		for (final BucketDescriptorPair bucketDescriptorPair : bucketDescriptorPairs) {
			nextParent = createChildBucket(nextParent, bucketDescriptorPair);
			assignBucketIdIfNecessary(nextParent);
		}
		nextParent.assignMember(memberId);
		return rootBucket;
	}

	public List<Bucket> getBucketTree() {
		return get().getBucketTree();
	}

	private Bucket createChildBucket(Bucket parentBucket, BucketDescriptorPair childDescriptorPair) {
		return parentBucket.addChildBucket(parentBucket.createChild(childDescriptorPair).withGenericRelation());
	}

	private void assignBucketIdIfNecessary(Bucket bucket) {
		if (idGenerationEnabled) {
			bucket.setBucketId(++currentId);
		}
	}
}
