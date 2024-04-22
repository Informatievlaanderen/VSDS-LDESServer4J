package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public interface TreeNodeStatementCreator {
    List<Statement> addEventStreamStatements(TreeNode treeNode, String baseUrl);

    List<Statement> addTreeNodeStatements(TreeNode treeNode, String collectionName, String prefix);
}
