package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNode;
import org.apache.jena.rdf.model.Model;

public interface TreeNodeConverter {
	Model toModel(final TreeNode treeNode);
}
