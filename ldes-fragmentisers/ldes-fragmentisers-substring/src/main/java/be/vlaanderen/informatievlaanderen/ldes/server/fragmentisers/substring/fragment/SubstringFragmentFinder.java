package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations.SubstringRelationsAttributer;

import java.util.List;

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

	public LdesFragment getOpenOrLastPossibleFragment(LdesFragment parentFragment,
			LdesFragment rootFragment, List<String> buckets) {
		if (rootFragment.getNumberOfMembers() < substringConfig.getMemberLimit())
			return rootFragment;
		LdesFragment currentParentFragment = rootFragment;
		LdesFragment currentChildFragment = null;
		for (String bucket : buckets) {
			currentChildFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment, bucket);
			substringRelationsAttributer.addSubstringRelation(currentParentFragment, currentChildFragment);
			if (currentChildFragment.getNumberOfMembers() < substringConfig.getMemberLimit()) {
				break;
			}
			currentParentFragment = currentChildFragment;
		}
		return currentChildFragment;
	}

}
