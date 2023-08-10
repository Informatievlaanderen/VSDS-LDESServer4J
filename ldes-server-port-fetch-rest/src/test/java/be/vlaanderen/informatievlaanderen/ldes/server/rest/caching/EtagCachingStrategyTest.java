package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.EventStreamInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeMemberList;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNodeInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.caching.EtagCachingStrategy;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EtagCachingStrategyTest {
	private static final LdesFragmentIdentifier node2 = new LdesFragmentIdentifier(
			new ViewName("collectionName", "node2"), List.of());
	private static final LdesFragmentIdentifier node3 = new LdesFragmentIdentifier(
			new ViewName("collectionName", "node3"), List.of());

	private static TreeNodeDto createViewTreeNodeDto(String viewName, List<String> treeNodeIdsInRelations,
			List<String> memberIds) {
		String eventStreamIdentifier = "/";
		String treeNodeIdentifier = "/" + viewName;
		EventStreamInfo eventStreamInfo = new EventStreamInfo(treeNodeIdentifier, eventStreamIdentifier,
				ModelFactory.createDefaultModel(), true, List.of(), List.of());
		TreeNodeInfo treeNodeInfo = new TreeNodeInfo(treeNodeIdentifier, List.of());
		TreeMemberList treeMemberList = new TreeMemberList(eventStreamIdentifier, List.of());
		return new TreeNodeDto(new TreeNode(eventStreamInfo, treeNodeInfo, treeMemberList), treeNodeIdentifier,
				treeNodeIdsInRelations, memberIds, false,
				true, "collectionName");
	}

	private static String createTreeRelation(LdesFragmentIdentifier node) {
		return node.asString();
	}

	@ParameterizedTest
	@ArgumentsSource(value = ETagEventStreamArgumentsProvider.class)
	void when_EventStreamIsRequested_thenACorrectEtagIsGenerated(String hostname, String collection,
			String language, String expectedEtag) {

		String etag = cachingStrategy(hostname).generateCacheIdentifier(collection, language);

		assertEquals(expectedEtag, etag);
	}

	@ParameterizedTest
	@ArgumentsSource(value = ETagTreeNodeArgumentsProvider.class)
	void when_TreeNodeIsRequested_thenACorrectEtagIsGenerated(String hostname, TreeNodeDto treeNodeDto,
			String language, String expectedEtag) {
		String etag = cachingStrategy(hostname).generateCacheIdentifier(treeNodeDto, language);

		assertEquals(expectedEtag, etag);
	}

	private EtagCachingStrategy cachingStrategy(String hostname) {
		return new EtagCachingStrategy(hostname);
	}

	static class ETagEventStreamArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("http://localhost:8080", "collection1",
							"text/turtle", "69a427a7acd03828f1c8a3f0f6727ae31e9e7df352f865d81c6fe022783bc8ef"),
					Arguments.of("http://localhost:8080", "collection1",
							"text/turtle", "69a427a7acd03828f1c8a3f0f6727ae31e9e7df352f865d81c6fe022783bc8ef"),
					Arguments.of("http://localhost:8080", "collection1",
							"application/n-quads", "9b5da23793852ec35e73ffe6c36a302d289d59009f05930850a88d7f4baa3926"));
		}
	}

	static class ETagTreeNodeArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("http://localhost:8080",
							createViewTreeNodeDto("view1",
									List.of(createTreeRelation(node2)),
									List.of("member1")),
							"text/turtle", "a406fc6c4562baf9bb6312ff195e7d964156ecf4ac7ec25d6e81731594e4205a"),
					Arguments.of("http://localhost:8080",
							createViewTreeNodeDto("view2",
									List.of(createTreeRelation(node2)),
									List.of("member1")),
							"text/turtle", "57b620546b58690b28931fe9db60257fe2b2e2477ea48b96b722e9a00a22a791"),
					Arguments.of("http://localhost:8080",
							createViewTreeNodeDto("view2",
									List.of(createTreeRelation(node2), createTreeRelation(node3)),
									List.of("member1")),
							"text/turtle", "1c9ed7f7dc03b9e58e577978f705810cb0ec0184cc74d3dd293e1b19fd15cd9a"),
					Arguments.of("http://localhost:8080",
							createViewTreeNodeDto("view1",
									List.of(createTreeRelation(node2)),
									List.of("member1", "member2")),
							"text/turtle", "67d101c55840eb638294b244f68286f7f62dee1e9aeec21aaa1f6aef732dccb8"),
					Arguments.of("http://localhost:8080",
							createViewTreeNodeDto("view1",
									List.of(createTreeRelation(node2)),
									List.of("member1", "member2")),
							"application/n-quads", "19cdab7a71dd21df12ed2aef54c6c5dd136910c807f0d7e2e59dc2f888716e23"));
		}
	}
}
