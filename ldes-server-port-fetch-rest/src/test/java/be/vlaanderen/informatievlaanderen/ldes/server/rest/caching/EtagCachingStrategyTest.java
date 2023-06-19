package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EtagCachingStrategyTest {

	private static TreeNode createView(String viewName) {
		return new TreeNode("/" + viewName, false, true, List.of(), List.of(), "collectionName");
	}

	private static TreeNode createView(String viewName, List<TreeRelation> relations, List<Member> members) {
		return new TreeNode("/" + viewName, false, true, relations, members, "collectionName");
	}

	private static TreeRelation createTreeRelation(String node) {
		return new TreeRelation(null, node, null, null, null);
	}

	private static Member createMember(String memberId) {
		return new Member(memberId, null, null, null,
				null, null, null);
	}

	@ParameterizedTest
	@ArgumentsSource(value = ETagEventStreamArgumentsProvider.class)
	void when_EventStreamIsRequested_thenACorrectEtagIsGenerated(String hostname, String collection,
			List<TreeNode> views, String language, String expectedEtag) {

		EventStream eventStream = new EventStream(collection, "", "", "", views);
		String etag = cachingStrategy(hostname).generateCacheIdentifier(eventStream.collection(), language);

		assertEquals(expectedEtag, etag);
	}

	@ParameterizedTest
	@ArgumentsSource(value = ETagTreeNodeArgumentsProvider.class)
	void when_TreeNodeIsRequested_thenACorrectEtagIsGenerated(String hostname, TreeNode treeNode,
			String language, String expectedEtag) {
		String etag = cachingStrategy(hostname).generateCacheIdentifier(treeNode, language);

		assertEquals(expectedEtag, etag);
	}

	private EtagCachingStrategy cachingStrategy(String hostname) {
		return new EtagCachingStrategy(hostname);
	}

	static class ETagEventStreamArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("http://localhost:8080", "collection1", List.of(),
							"text/turtle", "69a427a7acd03828f1c8a3f0f6727ae31e9e7df352f865d81c6fe022783bc8ef"),
					Arguments.of("http://localhost:8080", "collection1",
							List.of(createView("view1"), createView("view2")),
							"text/turtle", "69a427a7acd03828f1c8a3f0f6727ae31e9e7df352f865d81c6fe022783bc8ef"),
					Arguments.of("http://localhost:8080", "collection1",
							List.of(createView("view1"), createView("view2")),
							"application/n-quads", "9b5da23793852ec35e73ffe6c36a302d289d59009f05930850a88d7f4baa3926"));
		}
	}

	static class ETagTreeNodeArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("http://localhost:8080",
							createView("view1",
									List.of(createTreeRelation("node2")),
									List.of(createMember("member1"))),
							"text/turtle", "ad7a41b04ef70a0de74d473b476575576e8c58bfa2f79d729e88c33b981cb2cb"),
					Arguments.of("http://localhost:8080",
							createView("view2",
									List.of(createTreeRelation("node2")),
									List.of(createMember("member1"))),
							"text/turtle", "0a3a53bcb170c8b216f7ee043bb811a8b089e60203c81ffaaa3f64339da513d1"),
					Arguments.of("http://localhost:8080",
							createView("view2",
									List.of(createTreeRelation("node2"), createTreeRelation("node3")),
									List.of(createMember("member1"))),
							"text/turtle", "88635f10b457ec2539545a279ff8d1f8cb9c04814b4454412d20b66f8e726e69"),
					Arguments.of("http://localhost:8080",
							createView("view1",
									List.of(createTreeRelation("node2")),
									List.of(createMember("member1"), createMember("member2"))),
							"text/turtle", "b4f4bf39b350629af1bc439ea4db99156ba1cb3d3fac074e61000d5a0ce2d4a6"),
					Arguments.of("http://localhost:8080",
							createView("view1",
									List.of(createTreeRelation("node2")),
									List.of(createMember("member1"), createMember("member2"))),
							"application/n-quads", "2515a3a4308c44d4d4f95027ddedd25e749c70de7ff9c26e6970ba0b395ba445"));
		}
	}
}
