package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import java.util.Objects;

public class TreeRelation {
    private String treePath;
    private String treeValue;
    private String treeNode;
    private String rdfSyntaxType;

    public TreeRelation(String treePath, String treeNode, String treeValue, String relation) {
        this.treePath = treePath;
        this.treeNode = treeNode;
        this.treeValue = String.format("%s^^<http://www.w3.org/2001/XMLSchema#dateTime>",treeValue);
        this.rdfSyntaxType = relation;
    }

    public String getTreePath() {
        return treePath;
    }

    public String getTreeValue() {
        return treeValue;
    }

    public String getTreeNode() {
        return treeNode;
    }

    public String getRdfSyntaxType() {
        return rdfSyntaxType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeRelation that = (TreeRelation) o;
        return Objects.equals(treePath, that.treePath) && Objects.equals(treeValue, that.treeValue) && Objects.equals(treeNode, that.treeNode) && Objects.equals(rdfSyntaxType, that.rdfSyntaxType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treePath, treeValue, treeNode, rdfSyntaxType);
    }
}
