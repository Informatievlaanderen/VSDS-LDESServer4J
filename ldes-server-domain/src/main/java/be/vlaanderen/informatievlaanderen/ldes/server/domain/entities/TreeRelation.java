package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class TreeRelation {
    private final String treePath;
    private final String treeValue;
    private final String treeNode;
    private final String rdfSyntaxType;

    public TreeRelation(String treePath, String treeValue, String treeNode, String rdfSyntaxType) {
        this.treePath = treePath;
        this.treeValue = treeValue;
        this.treeNode = treeNode;
        this.rdfSyntaxType = rdfSyntaxType;
    }

    public Resource getTreePathAsResource() {
        return ResourceFactory.createResource(treePath);
    }

    public Resource getTreeValueAsStringLiteral() {
        return ResourceFactory.createResource(treeValue);
    }

    public Resource getTreeNodeAsResource() {
        return ResourceFactory.createResource(treeNode);
    }

    public Resource getRdfSyntaxTypeAsResource() {
        return ResourceFactory.createResource(rdfSyntaxType);
    }
}
