package be.vlaanderen.informatievlaanderen.ldes.server.domain.caching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;

@ActiveProfiles("test")
class EtagCachingStrategyTest {

	private EtagCachingStrategy cachingStrategy;

	@Test
	void when_EventStreamIsRequested_thenACorrectEtagIsGenerated() {
		String etag;

		etag = testGeneration("http://localhost:8080", "collection1", List.of(),
				"ea25cd96ae19575f021e951a584466fb6b5ff76450f16a004b876bf4021b96ca");
		etag = testGeneration("http://localhost:8080", "collection1", List.of("view1", "view2"),
				"ea25cd96ae19575f021e951a584466fb6b5ff76450f16a004b876bf4021b96ca");

	}

	private String testGeneration(String hostname, String collection, List<String> views, String expectedEtag) {
		setupCachingStrategy(hostname, collection);
		EventStream eventStream = new EventStream(collection, "", "", "", views);
		String etag = cachingStrategy.generateCacheIdentifier(eventStream);

		assertEquals(expectedEtag, etag);

		return etag;
	}

	private void setupCachingStrategy(String hostname, String collection) {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName(hostname);
		ldesConfig.setCollectionName(collection);

		cachingStrategy = new EtagCachingStrategy(ldesConfig);
	}
}
