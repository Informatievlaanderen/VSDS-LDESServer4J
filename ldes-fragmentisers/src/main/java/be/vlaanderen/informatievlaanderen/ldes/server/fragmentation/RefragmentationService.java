package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

/**
 * The RefragmentationService creates fragments for a view based upon the
 * existing members.
 */
public interface RefragmentationService {
	void refragmentMembersForView(LdesFragment rootFragmentForView, FragmentationStrategy fragmentationStrategyForView);
}
