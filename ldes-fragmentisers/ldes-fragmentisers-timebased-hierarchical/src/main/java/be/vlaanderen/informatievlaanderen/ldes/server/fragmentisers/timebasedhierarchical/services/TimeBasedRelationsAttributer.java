package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.TREE_INBETWEEN_RELATION;

public class TimeBasedRelationsAttributer implements RelationsAttributer {

	private final FragmentRepository fragmentRepository;

	private final TimeBasedConfig config;

	public TimeBasedRelationsAttributer(FragmentRepository fragmentRepository,
			TimeBasedConfig config) {
		this.fragmentRepository = fragmentRepository;
		this.config = config;
	}

	public void addInBetweenRelation(Fragment parentFragment, Fragment childFragment) {
		FragmentationTimestamp timestamp = timestampFromFragmentPairs(childFragment);
		TreeRelation parentChildRelation = new TreeRelation(config.getFragmentationPath(),
				childFragment.getFragmentId(),
				timestamp.asString(), timestamp.getType(),
				TREE_INBETWEEN_RELATION);
		saveRelation(parentFragment, parentChildRelation, timestamp.getNextUpdateTs());
	}

	public void addDefaultRelation(Fragment parentFragment, Fragment childFragment) {
		saveRelation(parentFragment, getDefaultRelation(childFragment), null);
	}

	private void saveRelation(Fragment fragment, TreeRelation relation, LocalDateTime nextUpdateTs) {
		if (!fragment.containsRelation(relation)) {
			// TODO TVB: moet afhankelijk zijn van configuratie
			fragmentRepository.makeChildrenImmutable(fragment);
			fragment.setNextUpdateTs(nextUpdateTs);
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
		LocalDateTime time = LocalDateTime.of(timeMap.getOrDefault(Granularity.YEAR.getValue(), 0),
				timeMap.getOrDefault(Granularity.MONTH.getValue(), 1),
				timeMap.getOrDefault(Granularity.DAY.getValue(), 1),
				timeMap.getOrDefault(Granularity.HOUR.getValue(), 0),
				timeMap.getOrDefault(Granularity.MINUTE.getValue(), 0),
				timeMap.getOrDefault(Granularity.SECOND.getValue(), 0));
		return new FragmentationTimestamp(time, Granularity.fromIndex(timeMap.size() - 1));
	}
}
