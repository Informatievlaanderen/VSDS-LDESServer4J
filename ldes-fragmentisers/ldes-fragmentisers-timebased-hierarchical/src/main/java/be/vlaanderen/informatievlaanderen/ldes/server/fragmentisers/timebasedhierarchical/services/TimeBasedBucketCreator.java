package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.HierarchicalTimeBasedFragmentationStrategy.TIMEBASED_FRAGMENTATION_HIERARCHICAL;

public class TimeBasedBucketCreator {
	private final BucketRepository bucketRepository;
	private final TimeBasedRelationsAttributer relationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedBucketCreator.class);

	public TimeBasedBucketCreator(BucketRepository bucketRepository, TimeBasedRelationsAttributer relationsAttributer) {
		this.bucketRepository = bucketRepository;
		this.relationsAttributer = relationsAttributer;
	}

	public Bucket getOrCreateFragment(Bucket parentFragment,
	                                    FragmentationTimestamp fragmentationTimestamp,
	                                    Granularity granularity) {
		return getOrCreateFragment(parentFragment, fragmentationTimestamp.getTimeValueForGranularity(granularity), granularity);
	}

	public Bucket getOrCreateFragment(Bucket parentBucket,
	                                    String timeValue,
	                                    Granularity granularity) {
		Bucket childBucket = parentBucket.createChild(new BucketDescriptorPair(granularity.getValue(), timeValue));
		return bucketRepository
				.retrieveBucket(childBucket.getBucketDescriptor())
				.orElseGet(() -> {
					bucketRepository.saveBucket(childBucket);
//					logFragmentation(parentBucket, child);
					return childBucket;
				});
	}

	// TODO
	private void addRelationToParent(Fragment parentFragment, Fragment child) {
		if (isDefaultBucket(child)) {
			relationsAttributer.addDefaultRelation(parentFragment, child);
		} else {
			relationsAttributer.addInBetweenRelation(parentFragment, child);
		}
	}

	private boolean isDefaultBucket(Fragment fragment) {
		return fragment.getValueOfKey(Granularity.YEAR.getValue()).orElse("").equals(DEFAULT_BUCKET_STRING);
	}

	private void logFragmentation(Fragment parentFragment, Fragment child) {
		String viewName = parentFragment.getViewName().asString();
		Metrics
				.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, TIMEBASED_FRAGMENTATION_HIERARCHICAL)
				.increment();
		LOGGER.debug("Timebased fragment created with id: {}", child.getFragmentId());
	}


}
