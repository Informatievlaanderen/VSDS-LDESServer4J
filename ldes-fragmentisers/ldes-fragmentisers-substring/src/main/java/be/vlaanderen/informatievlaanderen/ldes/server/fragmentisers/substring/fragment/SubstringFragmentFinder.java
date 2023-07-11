package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations.SubstringRelationsAttributer;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.ROOT_SUBSTRING;

public class SubstringFragmentFinder {
	private final SubstringFragmentCreator substringFragmentCreator;
	private final SubstringConfig substringConfig;
	private final SubstringRelationsAttributer substringRelationsAttributer;

	public SubstringFragmentFinder(SubstringFragmentCreator substringFragmentCreator, SubstringConfig substringConfig,
			SubstringRelationsAttributer substringRelationsAttributer) {
		this.substringFragmentCreator = substringFragmentCreator;
		this.substringConfig = substringConfig;
		this.substringRelationsAttributer = substringRelationsAttributer;
	}

	public Fragment getOpenOrLastPossibleFragment(Fragment parentFragment,
			Fragment rootFragment, List<String> buckets) {

		Fragment currentParentFragment = rootFragment;
		Fragment currentChildFragment = null;
		for (String bucket : buckets) {
			if (canBeAddedToRoot(rootFragment, bucket)) {
				return rootFragment;
			}
			if (ROOT_SUBSTRING.equals(bucket)) {
				continue;
			}

			currentChildFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment, bucket);
			substringRelationsAttributer.addSubstringRelation(currentParentFragment, currentChildFragment);
			if (currentChildFragment.getNumberOfMembers() < substringConfig.getMemberLimit()) {
				break;
			}
			currentParentFragment = currentChildFragment;
		}
		return currentChildFragment;
	}

	private boolean canBeAddedToRoot(Fragment rootFragment, String bucket) {
		return ROOT_SUBSTRING.equals(bucket)
				&& rootFragment.getNumberOfMembers() < substringConfig.getMemberLimit();
	}

}
