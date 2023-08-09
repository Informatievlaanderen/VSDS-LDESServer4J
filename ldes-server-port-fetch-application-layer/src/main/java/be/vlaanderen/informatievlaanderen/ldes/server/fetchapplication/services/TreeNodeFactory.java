package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;

public interface TreeNodeFactory {
	TreeNodeDto getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName);
}
