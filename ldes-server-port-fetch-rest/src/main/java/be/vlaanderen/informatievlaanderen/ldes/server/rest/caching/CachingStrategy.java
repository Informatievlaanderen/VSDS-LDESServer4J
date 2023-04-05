package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;

public interface CachingStrategy {

	String generateCacheIdentifier(EventStream eventStream);

	String generateCacheIdentifier(TreeNode treeNode);
}
