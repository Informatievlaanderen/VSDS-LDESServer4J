package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

public class TimeBasedFragmentFinder {
	private final TimeBasedFragmentCreator fragmentCreator;
	private final TimeBasedConfig config;

	public TimeBasedFragmentFinder(TimeBasedFragmentCreator fragmentCreator, TimeBasedConfig config) {
		this.fragmentCreator = fragmentCreator;
		this.config = config;
	}

	public Fragment getLowestFragment(Fragment parentFragment, FragmentationTimestamp fragmentationTimestamp,
			Granularity granularity) {
		if (isLowest(parentFragment)) {
			return parentFragment;
		}
		return getLowestFragment(
				fragmentCreator.getOrCreateFragment(parentFragment, fragmentationTimestamp, granularity),
				fragmentationTimestamp, granularity.getChild());
	}

	public Fragment getDefaultFragment(Fragment rootFragment) {
		return fragmentCreator.getOrCreateFragment(rootFragment, DEFAULT_BUCKET_STRING, Granularity.YEAR);
	}

	private boolean isLowest(Fragment fragment) {
		return fragment.getFragmentPairs().stream()
				.anyMatch(fragmentPair -> fragmentPair.fragmentKey().equals(config.getMaxGranularity().getValue()));
	}

}
