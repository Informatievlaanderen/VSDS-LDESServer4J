package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;

public interface CachingStrategy {

	String generateCacheIdentifier(EventStreamResponse eventStreamResponse);

	String generateCacheIdentifier(TreeNode treeNode);
}
