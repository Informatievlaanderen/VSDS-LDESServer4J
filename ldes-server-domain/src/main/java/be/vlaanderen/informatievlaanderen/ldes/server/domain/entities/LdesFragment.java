package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

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

    private final Map<String, String> ldesFragmentConfig;

    public LdesFragment(List<LdesMember> members, Map<String, String> ldesFragmentConfig) {
        this.members = members;
        this.ldesFragmentConfig = ldesFragmentConfig;
        addHeaders();
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

        members.stream().map(LdesMember::getModel).map(this::replaceViewId).forEach(model::add);

        return model;
    }

    private Model replaceViewId(Model memberModel) {
        Statement statement = memberModel.listStatements(null, TREE_MEMBER, (Resource) null).nextStatement();
        memberModel.remove(statement);
        memberModel.add(createStatement(createResource(ldesFragmentConfig.get("view")), statement.getPredicate(),
                statement.getObject()));
        return memberModel;
    }

    private void addHeaders() {
        if (!ldesFragmentConfig.containsKey("view")) {
            throw new RuntimeException("Fragment configuration: missing view");
        }
        Resource viewId = createResource(ldesFragmentConfig.get("view"));
        Resource fragmentId = createResource(viewId.toString() + "?fragment=1");

        if (ldesFragmentConfig.containsKey("shape")) {
            statements.add(createStatement(viewId, TREE_SHAPE, createResource(ldesFragmentConfig.get("shape"))));
        }
        statements.add(createStatement(viewId, TREE_VIEW, fragmentId));
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
