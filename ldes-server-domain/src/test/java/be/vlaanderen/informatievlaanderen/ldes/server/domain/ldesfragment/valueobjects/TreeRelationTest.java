package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TreeRelationTest {

	@Test
	@DisplayName("Test Equality of TreeRelations")
	void test_EqualityOfTreeRelations() {
		TreeRelation treeRelation = new TreeRelation("treePath", "treeNode", "treeValue", "treeValueType", "relation");
		TreeRelation otherTreeRelation = new TreeRelation("treePath", "treeNode", "treeValue", "treeValueType",
				"relation");
		assertEquals(treeRelation, otherTreeRelation);
		assertEquals(treeRelation, treeRelation);
		assertEquals(otherTreeRelation, otherTreeRelation);
	}

	@ParameterizedTest
	@ArgumentsSource(TreeRelationArgumentsProvider.class)
	void test_InequalityOfTreeRelations(Object otherTreeRelation) {
		TreeRelation treeRelation = new TreeRelation("treePath", "treeNode", "treeValue", "treeValueType", "relation");
		assertNotEquals(treeRelation, otherTreeRelation);
	}

	static class TreeRelationArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of(new Member("some_id", null, List.of())), Arguments.of((Object) null),
					Arguments.of(new TreeRelation("differentTreePath", "treeNode", "treeValue", "treeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath", "differentTreeNode", "treeValue", "treeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath", "treeNode", "differentTreeValue", "treeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath", "treeNode", "treeValue", "differentTreeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath", "treeNode", "treeValue", "treeValueType",
							"differentRelation")));
		}
	}

}
