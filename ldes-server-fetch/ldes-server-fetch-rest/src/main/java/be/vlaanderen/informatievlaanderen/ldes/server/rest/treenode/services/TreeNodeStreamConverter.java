package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.apache.jena.rdf.model.Model;

public interface TreeNodeStreamConverter {
    Model getMetaDataStatements(TreeNode treeNode);

    Model getMemberStatements(Member member, String treeNodeId);
}
