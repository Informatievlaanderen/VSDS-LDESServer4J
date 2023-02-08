package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
class EtagCachingStrategyTest {

	private EtagCachingStrategy cachingStrategy;

	@Test
	void when_EventStreamIsRequested_thenACorrectEtagIsGenerated() {
		String etag;

		etag = testGeneration("http://localhost:8080", "collection1", List.of(),
				"ea25cd96ae19575f021e951a584466fb6b5ff76450f16a004b876bf4021b96ca");
		etag = testGeneration("http://localhost:8080", "collection1",
				List.of(createView("view1"), createView("view2")),
				"ea25cd96ae19575f021e951a584466fb6b5ff76450f16a004b876bf4021b96ca");
	}

	private TreeNode createView(String viewName) {
		return new TreeNode("/" + viewName, false, false, true, List.of(), List.of());
	}

	private String testGeneration(String hostname, String collection, List<TreeNode> views, String expectedEtag) {
		setupCachingStrategy(hostname, collection);
		EventStreamResponse eventStreamResponse = new EventStreamResponse(
				new EventStreamInfoResponse(hostname + "/" + collection, "", "", "",
						views.stream().map(TreeNode::getFragmentId).toList()),
				views.stream().map(treeNode -> new TreeNodeInfoResponse(treeNode.getFragmentId(), List.of())).toList());
		String etag = cachingStrategy.generateCacheIdentifier(eventStreamResponse);

		assertEquals(expectedEtag, etag);

		return etag;
	}

	private void setupCachingStrategy(String hostname, String collection) {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName(hostname);
		ldesConfig.setCollectionName(collection);

		cachingStrategy = new EtagCachingStrategy();
	}
}
