package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.ChildBucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TimeBasedLinearCachingTriggered;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;
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

	public Bucket addInBetweenRelation(Bucket parentBucket, Bucket childBucket) {
		FragmentationTimestamp timestamp = timestampFromFragmentPairs(childBucket);
		final ChildBucket child = parentBucket.addChildBucket(childBucket.withRelation(createInBetweenRelations(timestamp)));
		triggerTimeBasedLinearCachingIfNeeded(parentBucket.getBucketId(), timestamp.getNextUpdateTs());
		return child;
	}

	public Bucket addDefaultRelation(Bucket parentBucket, Bucket childBucket) {
		final ChildBucket child = parentBucket.addChildBucket(childBucket.withGenericRelation());
		// TODO: use something else then bucket_id (as this probably won't be set yet)
		triggerTimeBasedLinearCachingIfNeeded(parentBucket.getBucketId(), null);
		return child;
	}

	private FragmentationTimestamp timestampFromFragmentPairs(Bucket bucket) {
		Map<String, Integer> timeMap = bucket.getBucketDescriptorPairs().stream()
				.filter(descriptorPair -> Arrays.stream(Granularity.values()).map(Granularity::getValue)
						.anyMatch(t -> t.equals(descriptorPair.key())))
				.collect(Collectors.toMap(BucketDescriptorPair::key, pair -> Integer.parseInt(pair.value())));
		return createTimestampFromMap(timeMap);
	}

	private TreeRelation[] createInBetweenRelations(FragmentationTimestamp timestamp) {
		return new TreeRelation[]{
				new TreeRelation(TREE_GTE_RELATION, timestamp.getTime().toString(), XSD_DATETIME, config.getFragmentationPath()),
				new TreeRelation(TREE_LT_RELATION, timestamp.getLtBoundary().toString(), XSD_DATETIME, config.getFragmentationPath()),
		};
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
