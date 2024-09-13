package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.TimeBasedLinearCachingTriggered;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.*;

public class TimeBasedRelationsAttributer implements RelationsAttributer {

	private final ApplicationEventPublisher applicationEventPublisher;

	private final TimeBasedConfig config;

	public TimeBasedRelationsAttributer(ApplicationEventPublisher applicationEventPublisher, TimeBasedConfig config) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.config = config;
	}

	public void addInBetweenRelation(Bucket parentBucket, Bucket childBucket) {
		FragmentationTimestamp timestamp = timestampFromFragmentPairs(childBucket);
		addInBetweenRelation(parentBucket, childBucket, TREE_GTE_RELATION, timestamp.getTime().toString());
		addInBetweenRelation(parentBucket, childBucket, TREE_LT_RELATION, timestamp.getLtBoundary().toString());
		triggerTimeBasedLinearCachingIfNeeded(parentBucket.getBucketId(), timestamp.getNextUpdateTs());
	}

	public void addDefaultRelation(Bucket parentBucket, Bucket childBucket) {
		parentBucket.addChildBucket(childBucket.withGenericRelation());
		triggerTimeBasedLinearCachingIfNeeded(parentBucket.getBucketId(), null);
	}

	private FragmentationTimestamp timestampFromFragmentPairs(Bucket bucket) {
		Map<String, Integer> timeMap = bucket.getBucketDescriptorPairs().stream()
				.filter(descriptorPair -> Arrays.stream(Granularity.values()).map(Granularity::getValue)
						.anyMatch(t -> t.equals(descriptorPair.key())))
				.collect(Collectors.toMap(BucketDescriptorPair::key, pair -> Integer.parseInt(pair.value())));
		return createTimestampFromMap(timeMap);
	}

	private void addInBetweenRelation(Bucket parentBucket, Bucket childBucket, String type, String timestamp) {
		final BucketRelation relation = new BucketRelation(
				type, timestamp, XSD_DATETIME, config.getFragmentationPath()
		);
		parentBucket.addChildBucket(childBucket.withRelation(relation));
	}

	private void triggerTimeBasedLinearCachingIfNeeded(long bucketId, LocalDateTime nextUpdateTs) {
		if (config.isLinearTimeCachingEnabled()) {
			applicationEventPublisher.publishEvent(new TimeBasedLinearCachingTriggered(bucketId, nextUpdateTs));
		}
	}

	private static @NotNull FragmentationTimestamp createTimestampFromMap(Map<String, Integer> timeMap) {
		LocalDateTime time = LocalDateTime.of(timeMap.getOrDefault(Granularity.YEAR.getValue(), 0),
				timeMap.getOrDefault(Granularity.MONTH.getValue(), 1),
				timeMap.getOrDefault(Granularity.DAY.getValue(), 1),
				timeMap.getOrDefault(Granularity.HOUR.getValue(), 0),
				timeMap.getOrDefault(Granularity.MINUTE.getValue(), 0),
				timeMap.getOrDefault(Granularity.SECOND.getValue(), 0));
		return new FragmentationTimestamp(time, Granularity.fromIndex(timeMap.size() - 1));
	}
}
