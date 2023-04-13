package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
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
		return new TreeNode("/" + viewName, false, false, true, List.of(), List.of(), "collectionName");
	}

	private String testGeneration(String hostname, String collection, List<TreeNode> views, String expectedEtag) {
		setupCachingStrategy(hostname, collection);
		EventStream eventStream = new EventStream(collection, "", "", "", views);
		String etag = cachingStrategy.generateCacheIdentifier(eventStream);

		assertEquals(expectedEtag, etag);

		return etag;
	}

	private void setupCachingStrategy(String hostname, String collection) {
		LdesConfig ldesConfig = new LdesConfig();
		LdesSpecification ldesSpecification = new LdesSpecification();
		ldesConfig.setCollections(List.of(ldesSpecification));
		ldesSpecification.setHostName(hostname);
		ldesSpecification.setCollectionName(collection);

		cachingStrategy = new EtagCachingStrategy(ldesConfig);
	}
}
