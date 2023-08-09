package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNode;

public interface CachingStrategy {

	String generateCacheIdentifier(String collectionName, String language);

	String generateCacheIdentifier(TreeNode treeNode, String language);
}
