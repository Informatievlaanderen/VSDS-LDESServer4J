package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class TreeRelation {
    private String treePath;
    private String treeValue;
    private String treeNode;
    private String rdfSyntaxType;

    private TreeRelation(){
    }

    public TreeRelation(LdesFragment fragment, String relation) {
        this.treePath = fragment.getFragmentInfo().getPath();
        this.treeNode = fragment.getFragmentId();
        this.treeValue = String.format("%s^^<http://www.w3.org/2001/XMLSchema#dateTime>",fragment.getFragmentInfo().getValue());
        this.rdfSyntaxType = relation;
    }

    public Resource getTreePathAsResource() {
        return ResourceFactory.createResource(treePath);
    }

    public Literal getTreeValueAsStringLiteral() {
        return ResourceFactory.createStringLiteral(treeValue);
    }

    public Resource getTreeNodeAsResource() {
        return ResourceFactory.createResource(treeNode);
    }

    public Resource getRdfSyntaxTypeAsResource() {
        return ResourceFactory.createResource(rdfSyntaxType);
    }
}
