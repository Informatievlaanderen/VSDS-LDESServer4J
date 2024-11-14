package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TreeNodePrefixCreatorImplTest {
	private TreeNodePrefixCreatorImpl treeNodePrefixCreator;

	@BeforeEach
	void setUp() {
		treeNodePrefixCreator = new TreeNodePrefixCreatorImpl("http://localhost:8080");
	}

	@Test
	void given_TreeNodeAsView_when_CreatePrefixes_then_ReturnValidMap() {
		final TreeNode treeNode = new TreeNode("/event-stream/timebased", true, true, List.of(), List.of(), "event-stream", null);

		Map<String, String> result = treeNodePrefixCreator.createPrefixes(treeNode);

		assertThat(result)
				.containsExactlyInAnyOrderEntriesOf(Map.of(
						"timebased", "http://localhost:8080/event-stream/timebased/",
						"event-stream", "http://localhost:8080/event-stream/"
				));
	}

	@Test
	void given_TreeNodeAsPage_when_CreatePrefixes_then_ReturnValidMap() {
		final TreeNode treeNode = new TreeNode("/event-stream/timebased?pageNumber=1", true, false, List.of(), List.of(), "event-stream", null);

		Map<String, String> result = treeNodePrefixCreator.createPrefixes(treeNode);

		assertThat(result)
				.containsExactlyInAnyOrderEntriesOf(Map.of(
						"timebased", "http://localhost:8080/event-stream/timebased/",
						"event-stream", "http://localhost:8080/event-stream/"
				));	}
}