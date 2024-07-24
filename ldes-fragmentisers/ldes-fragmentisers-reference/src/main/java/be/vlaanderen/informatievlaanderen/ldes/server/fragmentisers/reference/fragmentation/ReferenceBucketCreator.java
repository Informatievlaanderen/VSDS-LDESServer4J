package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategy.REFERENCE_FRAGMENTATION;

public class ReferenceBucketCreator {

	private final String fragmentKeyReference;
	public static final String FRAGMENT_KEY_REFERENCE_ROOT = "";
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceBucketCreator.class);
	private final BucketRepository fragmentRepository;
	private final ReferenceFragmentRelationsAttributer relationsAttributer;

	public ReferenceBucketCreator(BucketRepository fragmentRepository,
	                              ReferenceFragmentRelationsAttributer relationsAttributer,
	                              String fragmentKeyReference) {
		this.fragmentRepository = fragmentRepository;
		this.relationsAttributer = relationsAttributer;
		this.fragmentKeyReference = fragmentKeyReference;
	}

	public Bucket getOrCreateBucket(Bucket parentBucket, String reference, Bucket rootBucket) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(fragmentKeyReference, reference);
		return fragmentRepository
				.retrieveBucket(parentBucket.getViewName(), parentBucket.createChildDescriptor(childDescriptorPair))
				.orElseGet(() -> {
					final Bucket childBucket = fragmentRepository.insertBucket(parentBucket.createChild(childDescriptorPair));
					if (reference.equals(DEFAULT_BUCKET_STRING)) {
						relationsAttributer.addDefaultRelation(parentBucket, childBucket);
					} else {
						relationsAttributer.addRelationFromRootToBottom(rootBucket, childBucket);
					}
					logBucketation(parentBucket, childBucket);
					return childBucket;
				});
	}

	public Bucket getOrCreateRootBucket(Bucket parentBucket, String reference) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(fragmentKeyReference, reference);
		return fragmentRepository
				.retrieveBucket(parentBucket.getViewName(), parentBucket.createChildDescriptor(childDescriptorPair))
				.orElseGet(() -> {
					final Bucket childBucket = fragmentRepository.insertBucket(parentBucket.createChild(childDescriptorPair));
					logBucketation(parentBucket, childBucket);
					return childBucket;
				});
	}

	private void logBucketation(Bucket parentBucket, Bucket child) {
		String viewName = parentBucket.getViewName().asString();
		Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT,
				VIEW, viewName, FRAGMENTATION_STRATEGY, REFERENCE_FRAGMENTATION).increment();
		LOGGER.debug("Reference fragment created with id: {}", child.getBucketDescriptorAsString());
	}

}
