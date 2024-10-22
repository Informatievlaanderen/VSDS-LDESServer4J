package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

public class ReferenceBucketCreator {
	private final String fragmentKeyReference;
	public static final String FRAGMENT_KEY_REFERENCE_ROOT = "";
	private final ReferenceFragmentRelationsAttributer relationsAttributer;

	public ReferenceBucketCreator(ReferenceFragmentRelationsAttributer relationsAttributer,
	                              String fragmentKeyReference) {
		this.relationsAttributer = relationsAttributer;
		this.fragmentKeyReference = fragmentKeyReference;
	}

	public Bucket getOrCreateBucket(Bucket parentBucket, String reference, Bucket rootBucket) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(fragmentKeyReference, reference);
		final Bucket childBucket = parentBucket.createChild(childDescriptorPair);
		if (reference.equals(DEFAULT_BUCKET_STRING)) {
			return relationsAttributer.addDefaultRelation(parentBucket, childBucket);
		} else {
			return relationsAttributer.addRelationFromRootToBottom(rootBucket, childBucket);
		}
	}

	public Bucket getOrCreateRootBucket(Bucket parentBucket, String reference) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(fragmentKeyReference, reference);
		return parentBucket.createChild(childDescriptorPair);
	}

}
