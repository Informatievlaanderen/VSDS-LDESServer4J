package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

/**
 * The RefragmentationService creates fragments for a view based upon the
 * existing members.
 */
public interface RefragmentationService {
	void refragmentMembersForView(Fragment rootFragmentForView, FragmentationStrategy fragmentationStrategyForView);
}
