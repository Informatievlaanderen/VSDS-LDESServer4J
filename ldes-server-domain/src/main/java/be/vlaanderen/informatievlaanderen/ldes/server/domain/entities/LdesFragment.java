package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

public class LdesFragment {

    List<Statement> statements = new ArrayList<>();

    private final List<LdesMember> members;

    private List<TreeRelation> treeRelations = new LinkedList<>();

    public LdesFragment(List<LdesMember> members) {
        this.members = members;
        addHeaders();
    }

    public LdesFragment(List<LdesMember> members, List<TreeRelation> treeRelations) {
        this(members);
        this.treeRelations = treeRelations;
    }

    public List<LdesMember> getMembers() {
        return members;
    }

    public Model toRdfOutputModel() {
        Model model = ModelFactory.createDefaultModel();
        model.add(statements);

        members.stream().map(LdesMember::getModel).forEach(model::add);

        return model;
    }

    private void addHeaders() {
        Resource viewId = createResource("http://localhost:8089/exampleData");
        Resource fragmentId = createResource(viewId.toString() + "?fragment=1");

        statements.add(createStatement(viewId, TREE_VIEW, fragmentId));
        statements.add(createStatement(viewId, TREE_SHAPE, createResource("http://localhost:8089/exampleData/shape")));
        statements.add(createStatement(viewId, LDES_VERSION_OF, createResource("http://purl.org/dc/terms/isVersionOf")));
        statements.add(createStatement(viewId, LDES_TIMESTAMP_PATH, createResource("http://www.w3.org/ns/prov#generatedAtTime")));
        statements.add(createStatement(viewId, RDF_SYNTAX_TYPE, createResource("https://w3id.org/ldes#EventStream")));
        statements.add(createStatement(fragmentId, RDF_SYNTAX_TYPE, createResource("https://w3id.org/tree#Node")));


        treeRelations.forEach(treeRelation -> {
            Resource treeRelationNode = createResource();
            statements.add(createStatement(fragmentId, TREE_RELATION, treeRelationNode));
            statements.add(createStatement(treeRelationNode, TREE_VALUE, treeRelation.getTreeValueAsStringLiteral()));
            statements.add(createStatement(treeRelationNode, TREE_PATH, treeRelation.getTreePathAsResource()));
            statements.add(createStatement(treeRelationNode, TREE_NODE, treeRelation.getTreeNodeAsResource()));
            statements.add(createStatement(treeRelationNode, RDF_SYNTAX_TYPE, treeRelation.getRdfSyntaxTypeAsResource()));
        });
    }
}
