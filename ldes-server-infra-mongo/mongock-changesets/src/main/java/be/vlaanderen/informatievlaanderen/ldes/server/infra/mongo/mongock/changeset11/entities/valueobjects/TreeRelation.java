package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11.entities.valueobjects;

import java.util.Objects;

public record TreeRelation(String treePath, LdesFragmentIdentifier treeNode, String treeValue, String treeValueType, String relation) {
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TreeRelation that = (TreeRelation) o;
        return Objects.equals(treePath, that.treePath) && Objects.equals(treeValue, that.treeValue)
                && Objects.equals(treeNode, that.treeNode) && Objects.equals(relation, that.relation)
                && Objects.equals(treeValueType, that.treeValueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treePath, treeValue, treeNode, relation);
    }
}
