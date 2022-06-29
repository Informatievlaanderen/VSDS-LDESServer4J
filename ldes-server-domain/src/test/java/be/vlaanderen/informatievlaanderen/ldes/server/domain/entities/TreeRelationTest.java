package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

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

    @Test
    @DisplayName("Test Equality of TreeRelations")
    void test_EqualityOfTreeRelations() {
        TreeRelation treeRelation = new TreeRelation("treePath", "treeNode", "treeValue", "relation");
        TreeRelation otherTreeRelation = new TreeRelation("treePath", "treeNode", "treeValue", "relation");
        assertEquals(treeRelation, otherTreeRelation);
        assertEquals(treeRelation, treeRelation);
        assertEquals(otherTreeRelation, otherTreeRelation);
    }

    @ParameterizedTest
    @ArgumentsSource(TreeRelationArgumentsProvider.class)
    void test_InequalityOfTreeRelations(Object otherTreeRelation) {
        TreeRelation treeRelation = new TreeRelation("treePath", "treeNode", "treeValue", "relation");
        assertNotEquals(treeRelation, otherTreeRelation);
    }

    static class TreeRelationArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(new LdesMember(null)),
                    Arguments.of(new TreeRelation("differentTreePath", "treeNode", "treeValue", "relation")),
                    Arguments.of(new TreeRelation("treePath", "differentTreeNode", "treeValue", "relation")),
                    Arguments.of(new TreeRelation("treePath", "treeNode", "differentTreeValue", "relation")),
                    Arguments.of(new TreeRelation("treePath", "treeNode", "treeValue", "differentRelation")));
        }
    }

}