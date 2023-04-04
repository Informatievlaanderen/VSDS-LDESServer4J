package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeTest {
	private static final String ID = "FRAGMENT_ID";

	@Test
	void test_EqualityOfTreeNodes() {
		var treeNode = new TreeNode(ID, false, false, true, List.of(), List.of());
		var treeNode2 = new TreeNode(ID, true, false, true, null, List.of());

		assertEquals(treeNode, treeNode2);
		assertEquals(treeNode, treeNode);
		assertEquals(treeNode2, treeNode2);
	}

	@ParameterizedTest
	@ArgumentsSource(TreeNodeArgumentProvider.class)
	void test_InequalityOfTreeNodes(Object otherTreeNode) {
		var treeNode = new TreeNode(ID, false, false, false, null, List.of());

		assertNotEquals(treeNode, otherTreeNode);

	}

	static class TreeNodeArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of((Object) null),
					Arguments.of(new EventStream("collection", "timestamp", "path", "shape", List.of())),
					Arguments.of(new TreeNode("Other id", true, true, false, List.of(), null)),
					Arguments.of(new TreeNode("Another id", false, false, false, null, List.of())));
		}
	}
}