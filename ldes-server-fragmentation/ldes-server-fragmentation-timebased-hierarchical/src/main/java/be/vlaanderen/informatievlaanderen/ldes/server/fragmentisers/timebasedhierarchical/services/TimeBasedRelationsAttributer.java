package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.*;

public class TimeBasedRelationsAttributer implements RelationsAttributer {

	private final TimeBasedConfig config;

	public TimeBasedRelationsAttributer(TimeBasedConfig config) {
		this.config = config;
	}

	public Bucket addInBetweenRelation(Bucket parentBucket, Bucket childBucket) {
		FragmentationTimestamp timestamp = timestampFromFragmentPairs(childBucket);
		return parentBucket.addChildBucket(childBucket.withRelations(createTimeBasedRelations(timestamp)));
	}

	public Bucket addDefaultRelation(Bucket parentBucket, Bucket childBucket) {
		return parentBucket.addChildBucket(childBucket.withGenericRelation());
	}

	private FragmentationTimestamp timestampFromFragmentPairs(Bucket bucket) {
		Map<String, Optional<Integer>> timeMap = Arrays.stream(Granularity.values())
				.map(Granularity::getValue)
				.collect(Collectors.toMap(Function.identity(), key -> bucket.getValueForKey(key).map(Integer::parseInt)));
		return createTimestampFromMap(timeMap);
	}

	private TreeRelation[] createTimeBasedRelations(FragmentationTimestamp timestamp) {
		return new TreeRelation[]{
				new TreeRelation(TREE_GTE_RELATION, timestamp.getTime().toString(), XSD_DATETIME, config.getFragmentationPath()),
				new TreeRelation(TREE_LT_RELATION, timestamp.getLtBoundary().toString(), XSD_DATETIME, config.getFragmentationPath()),
		};
	}

	private static @NotNull FragmentationTimestamp createTimestampFromMap(Map<String, Optional<Integer>> timeMap) {
		LocalDateTime time = LocalDateTime.of(timeMap.get(Granularity.YEAR.getValue()).orElse(0),
				timeMap.get(Granularity.MONTH.getValue()).orElse(1),
				timeMap.get(Granularity.DAY.getValue()).orElse(1),
				timeMap.get(Granularity.HOUR.getValue()).orElse(0),
				timeMap.get(Granularity.MINUTE.getValue()).orElse(0),
				timeMap.get(Granularity.SECOND.getValue()).orElse(0));
		return new FragmentationTimestamp(time, Granularity.fromIndex((int) (timeMap.values().stream().filter(Optional::isPresent).count() - 1)));
	}
}
