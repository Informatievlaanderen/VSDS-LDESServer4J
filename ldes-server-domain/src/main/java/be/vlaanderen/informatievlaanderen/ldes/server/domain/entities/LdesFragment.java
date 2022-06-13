package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class LdesFragment {

    List<Statement> statements = new ArrayList<>();

    private final List<LdesMember> members;

    private List<TreeRelation> treeRelations = new LinkedList<>();

    public LdesFragment(List<LdesMember> members, Map<String, String> ldesFragmentConfig) {
        this.members = members;
        addHeaders(ldesFragmentConfig);
    }

    public LdesFragment(List<LdesMember> members, Map<String, String> ldesFragmentConfig,
                        List<TreeRelation> treeRelations) {
        this(members, ldesFragmentConfig);
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

    private void addHeaders(Map<String, String> ldesFragmentConfig) {
        Resource viewId = createResource(ldesFragmentConfig.get("view"));
        Resource fragmentId = createResource(viewId.toString() + "?fragment=1");

        statements.add(createStatement(viewId, TREE_VIEW, fragmentId));
        statements.add(createStatement(viewId, TREE_SHAPE, createResource(ldesFragmentConfig.get("shape"))));
        statements
                .add(createStatement(viewId, LDES_VERSION_OF, createResource("http://purl.org/dc/terms/isVersionOf")));
        statements.add(createStatement(viewId, LDES_TIMESTAMP_PATH,
                createResource("http://www.w3.org/ns/prov#generatedAtTime")));
        statements.add(createStatement(viewId, RDF_SYNTAX_TYPE, createResource("https://w3id.org/ldes#EventStream")));
        statements.add(createStatement(fragmentId, RDF_SYNTAX_TYPE, createResource("https://w3id.org/tree#Node")));

        Resource treeRelationNode = createResource();
        if (!treeRelations.isEmpty()) {
            statements.add(createStatement(fragmentId, TREE_RELATION, treeRelationNode));
        }
        treeRelations.forEach(treeRelation -> {
            statements.add(createStatement(treeRelationNode, TREE_VALUE, treeRelation.getTreeValueAsStringLiteral()));
            statements.add(createStatement(treeRelationNode, TREE_PATH, treeRelation.getTreePathAsResource()));
            statements.add(createStatement(treeRelationNode, TREE_NODE, treeRelation.getTreeNodeAsResource()));
            statements
                    .add(createStatement(treeRelationNode, RDF_SYNTAX_TYPE, treeRelation.getRdfSyntaxTypeAsResource()));
        });
    }
}
