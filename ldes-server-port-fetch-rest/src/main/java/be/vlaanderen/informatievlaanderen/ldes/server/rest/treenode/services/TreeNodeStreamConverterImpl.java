package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeStreamConverterImpl implements TreeNodeStreamConverter {
    @Value(HOST_NAME_KEY)
    private String prefix;
    private final TreeNodeStatementCreator treeNodeStatementCreator;

    public TreeNodeStreamConverterImpl(TreeNodeStatementCreator treeNodeStatementCreator) {
        this.treeNodeStatementCreator = treeNodeStatementCreator;
    }

    @Override
    public Model getMetaDataStatements(TreeNode treeNode) {
        Model metadataModel = createDefaultModel().add(getTreeNodeStatement(treeNode.getFragmentId()));
        metadataModel.add(treeNodeStatementCreator.addTreeNodeStatements(treeNode, treeNode.getCollectionName(), prefix));
        if (!treeNode.isView()) {
            metadataModel.add(treeNodeStatementCreator.addEventStreamStatements(treeNode, prefix + "/" + treeNode.getCollectionName()));
        }
        return metadataModel;
    }

    @Override
    public Model getMemberStatements(Member member, String collectionName) {
        Model memberModel = member.model();
        memberModel.add(createStatement(createResource(prefix + "/" + collectionName), TREE_MEMBER, createResource(member.getMemberIdWithoutPrefix())));
        return memberModel;
    }

    private Statement getTreeNodeStatement(String treeNodeId) {
        return createStatement(createResource(treeNodeId), RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE));
    }
}
