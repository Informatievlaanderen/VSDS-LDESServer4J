package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TreeRelationTest {

	private static final String TREENODE_REF = "/ref/treeNode";
	private static final String DIF_TREENODE_REF = "/ref/othertreeNode";

	@Test
	@DisplayName("Test Equality of TreeRelations")
	void test_EqualityOfTreeRelations() {
		TreeRelation treeRelation = new TreeRelation("treePath", LdesFragmentIdentifier.fromFragmentId(TREENODE_REF),
				"treeValue", "treeValueType", "relation");
		TreeRelation otherTreeRelation = new TreeRelation("treePath",
				LdesFragmentIdentifier.fromFragmentId(TREENODE_REF),
				"treeValue", "treeValueType",
				"relation");
		assertEquals(treeRelation, otherTreeRelation);
		assertEquals(treeRelation, treeRelation);
		assertEquals(otherTreeRelation, otherTreeRelation);
	}

	@ParameterizedTest
	@ArgumentsSource(TreeRelationArgumentsProvider.class)
	void test_InequalityOfTreeRelations(Object otherTreeRelation) {
		TreeRelation treeRelation = new TreeRelation("treePath", LdesFragmentIdentifier.fromFragmentId(TREENODE_REF),
				"treeValue", "treeValueType", "relation");
		assertNotEquals(treeRelation, otherTreeRelation);
	}

	static class TreeRelationArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of("otherClass"),
					Arguments.of((Object) null),
					Arguments.of(new TreeRelation("differentTreePath",
							LdesFragmentIdentifier.fromFragmentId(TREENODE_REF), "treeValue", "treeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath",
							LdesFragmentIdentifier.fromFragmentId(DIF_TREENODE_REF), "treeValue", "treeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath", LdesFragmentIdentifier.fromFragmentId(TREENODE_REF),
							"differentTreeValue", "treeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath", LdesFragmentIdentifier.fromFragmentId(TREENODE_REF),
							"treeValue", "differentTreeValueType",
							"relation")),
					Arguments.of(new TreeRelation("treePath", LdesFragmentIdentifier.fromFragmentId(TREENODE_REF),
							"treeValue", "treeValueType",
							"differentRelation")));
		}
	}

}
