package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class TreeNodeStreamConverterImpl {
    private final String prefix;
    private final String collectionName;
    private final String treeNodeId;

    public TreeNodeStreamConverterImpl(String collectionName, PrefixConstructor prefixConstructor, String treeNodeId) {
        this.prefix = prefixConstructor.buildPrefix();
        this.collectionName = collectionName;
        this.treeNodeId = treeNodeId;
    }

    public Model getMetaDataStatements(TreeNode treeNode) {
        Model metadataModel = createDefaultModel().add(getTreeNodeStatement());
        if (!treeNode.isView()) {
            metadataModel.add(getViewStatement("change"));
        }
        treeNode.getRelations().stream().map(this::getRelationStatements).forEach(metadataModel::add);
        return metadataModel;
    }

    private Statement getTreeNodeStatement() {
        return createStatement(createResource(treeNodeId), RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE));
    }

    private Statement getViewStatement(String viewId) {
        return createStatement(createResource(viewId), RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI));
    }

    private List<Statement> getRelationStatements(TreeRelation relation) {
        return TreeRelationResponse.fromRelation(relation, prefix).convertToStatements(treeNodeId);
    }

    public Model getMemberStatements(Member member) {
        Model memberModel = member.model();
        memberModel.add(createStatement(createResource(treeNodeId), TREE_MEMBER, createResource(member.getMemberIdWithoutPrefix())));
        return memberModel;
    }
}
