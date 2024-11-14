package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;

import java.util.Map;

public interface TreeNodePrefixCreator {
	Map<String, String> createPrefixes(TreeNode treeNode);
}
