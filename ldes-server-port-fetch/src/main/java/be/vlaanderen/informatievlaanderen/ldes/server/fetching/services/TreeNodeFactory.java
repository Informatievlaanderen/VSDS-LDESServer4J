package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;

public interface TreeNodeFactory {
	TreeNode getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName);
}
