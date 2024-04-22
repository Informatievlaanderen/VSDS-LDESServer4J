package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeStreamConverterImpl implements TreeNodeStreamConverter {
    @Value(HOST_NAME_KEY)
    private String prefix;

    @Override
    public Model getMetaDataStatements(TreeNode treeNode) {
        Model metadataModel = createDefaultModel().add(getTreeNodeStatement(treeNode.getFragmentId()));
        if (!treeNode.isView()) {
            metadataModel.add(getViewStatement(treeNode.getCollectionName()));
        }
        treeNode.getRelations().stream().map(relation -> getRelationStatements(relation, treeNode.getFragmentId())).forEach(metadataModel::add);
        return metadataModel;
    }

    @Override
    public Model getMemberStatements(Member member, String treeNodeId) {
        Model memberModel = member.model();
        memberModel.add(createStatement(createResource(prefix + treeNodeId), TREE_MEMBER, createResource(member.getMemberIdWithoutPrefix())));
        return memberModel;
    }

    private Statement getTreeNodeStatement(String treeNodeId) {
        return createStatement(createResource(prefix + treeNodeId), RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE));
    }

    private Statement getViewStatement(String viewId) {
        return createStatement(createResource(prefix + viewId), RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI));
    }

    private List<Statement> getRelationStatements(TreeRelation relation, String treeNodeId) {
        return TreeRelationResponse.fromRelation(relation, prefix).convertToStatements(treeNodeId);
    }
}
