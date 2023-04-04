package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.apache.jena.rdf.model.Model;

public interface TreeNodeConverter {
	Model toModel(final TreeNode treeNode);
}
