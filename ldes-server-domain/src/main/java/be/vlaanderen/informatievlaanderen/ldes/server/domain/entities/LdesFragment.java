package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.FragmentIdConverter.toFragmentId;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class LdesFragment {

    private final String fragmentId;

    private final FragmentInfo fragmentInfo;

    private final List<Statement> statements;

    private final List<String> memberIds;

    private final List<TreeRelation> relations;

    public LdesFragment(String fragmentId, FragmentInfo fragmentInfo) {
        this.fragmentId = fragmentId;
        this.fragmentInfo = fragmentInfo;
        this.statements = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.memberIds = new ArrayList<>();
    }

    public static LdesFragment newFragment(String hostname, FragmentInfo fragmentInfo) {
        String fragmentId = toFragmentId(hostname, fragmentInfo.getCollectionName(), fragmentInfo.getPath(),
                fragmentInfo.getValue());
        return new LdesFragment(fragmentId, fragmentInfo);
    }

    public void addRelation(TreeRelation treeRelation) {
        this.relations.add(treeRelation);
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public FragmentInfo getFragmentInfo() {
        return fragmentInfo;
    }

    public List<TreeRelation> getRelations() {
        return relations;
    }

    public List<String> getMembers() {
        return memberIds;
    }

    public void addMember(String ldesMemberId) {
        memberIds.add(ldesMemberId);
    }

    public Model toRdfOutputModel() {
        addFragmentMetadata();
        Model model = ModelFactory.createDefaultModel();
        model.add(statements);

//        members.stream().map(LdesMember::getModel).forEach(model::add);

        return model;
    }

    private void addFragmentMetadata() {
        Resource viewId = createResource(fragmentInfo.getView());
        Resource currrentFragmentId = createResource(this.fragmentId);

        if (fragmentInfo.getShape() != null) {
            statements.add(createStatement(viewId, TREE_SHAPE, createResource(fragmentInfo.getShape())));
        }
        if (fragmentInfo.getValue() != null)
            statements.add(createStatement(viewId, TREE_VIEW, currrentFragmentId));
        statements
                .add(createStatement(viewId, LDES_VERSION_OF, createResource("http://purl.org/dc/terms/isVersionOf")));
        statements.add(createStatement(viewId, LDES_TIMESTAMP_PATH,
                createResource("http://www.w3.org/ns/prov#generatedAtTime")));
        statements.add(createStatement(viewId, RDF_SYNTAX_TYPE, createResource("https://w3id.org/ldes#EventStream")));
        relations.forEach(treeRelation -> {
            Resource treeRelationNode = createResource();
            statements.add(createStatement(currrentFragmentId, TREE_RELATION, treeRelationNode));
            statements.add(createStatement(treeRelationNode, TREE_VALUE, createResource(treeRelation.getTreeValue())));
            statements.add(createStatement(treeRelationNode, TREE_PATH, createResource(treeRelation.getTreePath())));
            statements.add(createStatement(treeRelationNode, TREE_NODE, createResource(treeRelation.getTreeNode())));
            statements
                    .add(createStatement(treeRelationNode, RDF_SYNTAX_TYPE, createResource(treeRelation.getRelation())));
        });
    }

    public int getCurrentNumberOfMembers() {
        return memberIds.size();
    }

    public void setImmutable(boolean immutable) {
        this.fragmentInfo.setImmutable(immutable);
    }

    public boolean isImmutable() {
        return this.fragmentInfo.getImmutable();
    }

    public boolean isExistingFragment() {
        return this.fragmentInfo.getValue()!=null;
    }
}
