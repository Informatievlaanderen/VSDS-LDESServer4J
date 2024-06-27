package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.*;

public class TimeBasedRelationsAttributer implements RelationsAttributer {

	private final FragmentRepository fragmentRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	private final TimeBasedConfig config;

	public TimeBasedRelationsAttributer(FragmentRepository fragmentRepository,
	                                    ApplicationEventPublisher applicationEventPublisher,
	                                    TimeBasedConfig config) {
		this.fragmentRepository = fragmentRepository;
		this.applicationEventPublisher = applicationEventPublisher;
		this.config = config;
	}

	public void addInBetweenRelation(Fragment parentFragment, Fragment childFragment) {
		FragmentationTimestamp timestamp = timestampFromFragmentPairs(childFragment);
		TreeRelation parentGteRelation = new TreeRelation(config.getFragmentationPath(),
				childFragment.getFragmentId(),
				timestamp.getTime().toString(), XSD_DATETIME,
				TREE_GTE_RELATION);
		TreeRelation parentLtRelation = new TreeRelation(config.getFragmentationPath(),
				childFragment.getFragmentId(),
				timestamp.getLtBoundary().toString(), XSD_DATETIME,
				TREE_LT_RELATION);
		saveRelation(parentFragment, parentGteRelation, timestamp.getNextUpdateTs());
		saveRelation(parentFragment, parentLtRelation, timestamp.getNextUpdateTs());
	}

	public void addInBetweenRelation(Bucket parentBucket, Bucket childBucket) {
		FragmentationTimestamp timestamp = timestampFromFragmentPairs(childBucket);
		BucketRelation parentGteRelation = new BucketRelation(
				parentBucket,
				childBucket,
				TREE_GTE_RELATION,
				timestamp.getTime().toString(),
				XSD_DATETIME,
				config.getFragmentationPath()
		);
		BucketRelation parentLtRelation = new BucketRelation(
				parentBucket,
				childBucket,
				TREE_LT_RELATION,
				timestamp.getLtBoundary().toString(),
				XSD_DATETIME,
				config.getFragmentationPath()
		);
		applicationEventPublisher.publishEvent(new BucketCreatedEvent(parentGteRelation));
		applicationEventPublisher.publishEvent(new BucketCreatedEvent(parentLtRelation));
	}

	public void addDefaultRelation(Bucket parentBucket, Bucket childBucket) {
		final BucketRelation defaultRelation = BucketRelation.createGenericRelation(parentBucket, childBucket);
		applicationEventPublisher.publishEvent(new BucketCreatedEvent(defaultRelation));
	}

	public void addDefaultRelation(Fragment parentFragment, Fragment childFragment) {
		saveRelation(parentFragment, getDefaultRelation(childFragment), null);
	}

	// TODO: move this to pagination
	private void saveRelation(Fragment fragment, TreeRelation relation, LocalDateTime nextUpdateTs) {
		if (!fragment.containsRelation(relation)) {
			if (config.isLinearTimeCachingEnabled()) {
				fragmentRepository.makeChildrenImmutable(fragment);
				fragment.setNextUpdateTs(nextUpdateTs);
			}

			fragment.addRelation(relation);
			fragmentRepository.saveFragment(fragment);
		}
	}

	private FragmentationTimestamp timestampFromFragmentPairs(Fragment fragment) {
		Map<String, Integer> timeMap = new HashMap<>();
		fragment.getFragmentPairs().stream()
				.filter(fragmentPair -> Arrays.stream(Granularity.values()).map(Granularity::getValue)
						.anyMatch(t -> t.equals(fragmentPair.fragmentKey())))
				.forEach(pair -> timeMap.put(pair.fragmentKey(), Integer.valueOf(pair.fragmentValue())));
		return createTimestampFromMap(timeMap);
	}

	private FragmentationTimestamp timestampFromFragmentPairs(Bucket bucket) {
		Map<String, Integer> timeMap = bucket.getBucketDescriptorPairs().stream()
				.filter(descriptorPair -> Arrays.stream(Granularity.values()).map(Granularity::getValue)
						.anyMatch(t -> t.equals(descriptorPair.key())))
				.collect(Collectors.toMap(BucketDescriptorPair::key, pair -> Integer.parseInt(pair.value())));
		return createTimestampFromMap(timeMap);
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
