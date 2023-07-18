package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;

public interface CachingStrategy {

	String generateCacheIdentifier(String collectionName, String language);

	String generateCacheIdentifier(TreeNode treeNode, String language);
}
