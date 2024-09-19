package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

public class TimeBasedBucketFinder {
	private final TimeBasedBucketCreator bucketCreator;
	private final TimeBasedConfig config;

	public TimeBasedBucketFinder(TimeBasedBucketCreator bucketCreator, TimeBasedConfig config) {
		this.bucketCreator = bucketCreator;
		this.config = config;
	}

	public Bucket getLowestBucket(Bucket parentFragment, FragmentationTimestamp fragmentationTimestamp,
	                              Granularity granularity) {
		if (isLowest(parentFragment)) {
			return parentFragment;
		}
		return getLowestBucket(
				bucketCreator.getOrCreateBucket(parentFragment, fragmentationTimestamp, granularity),
				fragmentationTimestamp, granularity.getChild());
	}

	public Bucket getDefaultFragment(Bucket rootFragment) {
		return bucketCreator.getOrCreateBucket(rootFragment, DEFAULT_BUCKET_STRING, Granularity.YEAR);
	}

	private boolean isLowest(Bucket bucket) {
		return bucket.getValueForKey(config.getMaxGranularity().getValue()).isPresent();
	}
}
