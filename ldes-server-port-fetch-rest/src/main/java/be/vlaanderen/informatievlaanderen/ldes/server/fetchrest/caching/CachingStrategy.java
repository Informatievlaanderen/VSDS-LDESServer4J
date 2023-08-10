package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.valueobjects.TreeNodeDto;

public interface CachingStrategy {

	String generateCacheIdentifier(String collectionName, String language);

	String generateCacheIdentifier(TreeNodeDto treeNodeDto, String language);
}
