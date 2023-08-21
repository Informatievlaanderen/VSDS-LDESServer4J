package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;

public interface TreeNodeFetcher {
	TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest);
}
