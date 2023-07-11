package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.entities.TreeNode;
import org.apache.jena.rdf.model.Model;

public interface TreeNodeConverter {
	Model toModel(final TreeNode treeNode);
}
