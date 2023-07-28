package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

/**
 * The RefragmentationService creates fragments for a view based upon the
 * existing members.
 */
public interface RefragmentationService {
	void refragmentMembersForView(ViewName viewName, FragmentationStrategy fragmentationStrategyForView);
}
