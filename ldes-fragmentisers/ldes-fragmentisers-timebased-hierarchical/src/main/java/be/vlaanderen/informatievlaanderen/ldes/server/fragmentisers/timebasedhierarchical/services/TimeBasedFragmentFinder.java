package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;

public class TimeBasedFragmentFinder {

	private final TimeBasedFragmentCreator fragmentCreator;
	private final TimeBasedConfig config;

	public TimeBasedFragmentFinder(TimeBasedFragmentCreator fragmentCreator, TimeBasedConfig config) {
		this.fragmentCreator = fragmentCreator;
		this.config = config;
	}

	public Fragment getLowestFragment(Fragment parentFragment, FragmentationTimestamp fragmentationTimestamp,
			int granularity) {
		if (isLowest(parentFragment)) {
			return parentFragment;
		}
		return getLowestFragment(
				fragmentCreator.getOrCreateFragment(parentFragment, fragmentationTimestamp, granularity),
				fragmentationTimestamp, granularity + 1);
	}

	private boolean isLowest(Fragment fragment) {
		return fragment.getFragmentPairs().stream()
				.anyMatch(fragmentPair -> fragmentPair.fragmentKey().equals(config.getMaxGranularity()));
	}

}
