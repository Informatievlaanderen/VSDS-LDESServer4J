package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNode;

public interface TreeNodeFetcher {
	TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest);
}
