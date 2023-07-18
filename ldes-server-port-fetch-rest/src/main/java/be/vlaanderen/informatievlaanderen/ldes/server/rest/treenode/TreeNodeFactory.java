package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.entities.TreeNode;

public interface TreeNodeFactory {
	TreeNode getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName);
}
