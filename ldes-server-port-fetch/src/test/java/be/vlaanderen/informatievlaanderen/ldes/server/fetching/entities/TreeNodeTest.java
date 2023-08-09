package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

class TreeNodeTest {
	private static final String ID = "FRAGMENT_ID";

	@Test
	void test_EqualityOfTreeNodes() {
		var treeNode = new TreeNode(ID, false, true, List.of(), List.of(), "collectionName");
		var treeNode2 = new TreeNode(ID, true, true, null, List.of(), "collectionName");

		Assertions.assertEquals(treeNode, treeNode2);
		Assertions.assertEquals(treeNode, treeNode);
		Assertions.assertEquals(treeNode2, treeNode2);
	}

	@ParameterizedTest
	@ArgumentsSource(TreeNodeArgumentProvider.class)
	void test_InequalityOfTreeNodes(Object otherTreeNode) {
		var treeNode = new TreeNode(ID, false, false, null, List.of(), "collectionName");

		Assertions.assertNotEquals(treeNode, otherTreeNode);

	}

	static class TreeNodeArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of((Object) null),
					Arguments.of(new BigDecimal(0)),
					Arguments.of(new TreeNode("Other id", true, false, List.of(), null, "collectionName")),
					Arguments.of(new TreeNode("Another id", false, false, null, List.of(), "collectionName")));
		}
	}
}