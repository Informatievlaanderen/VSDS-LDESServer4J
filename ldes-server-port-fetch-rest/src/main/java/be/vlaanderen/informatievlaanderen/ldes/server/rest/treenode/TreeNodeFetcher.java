package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;

public interface TreeNodeFetcher {
	TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest);
}
