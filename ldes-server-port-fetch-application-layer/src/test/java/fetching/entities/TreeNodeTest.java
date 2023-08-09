package fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
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
		var treeNode = new TreeNodeDto(null, ID, List.of(), List.of(), false, true, List.of(), "collectionName");
		var treeNode2 = new TreeNodeDto(null, ID, List.of(), List.of(), true, true, List.of(), "collectionName");

		Assertions.assertEquals(treeNode, treeNode2);
		Assertions.assertEquals(treeNode, treeNode);
		Assertions.assertEquals(treeNode2, treeNode2);
	}

	@ParameterizedTest
	@ArgumentsSource(TreeNodeArgumentProvider.class)
	void test_InequalityOfTreeNodes(Object otherTreeNode) {
		var treeNode = new TreeNodeDto(null, ID, List.of(), List.of(), false, false, List.of(), "collectionName");

		Assertions.assertNotEquals(treeNode, otherTreeNode);

	}

	static class TreeNodeArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of((Object) null),
					Arguments.of(new BigDecimal(0)),
					Arguments.of(new TreeNodeDto(null, "Other id", List.of(), List.of(), true, false, null,
							"collectionName")),
					Arguments.of(
							new TreeNodeDto(null, "Another id", List.of(), List.of(), false, false, List.of(),
									"collectionName")));
		}
	}
}