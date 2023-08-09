package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;

public class TreeNode {
	private final TreeNodeInfo treeNodeInfo;

	public TreeNode(TreeNodeInfo treeNodeInfo) {
		this.treeNodeInfo = treeNodeInfo;
	}

	public Model getModel() {
		Model model = ModelFactory.createDefaultModel();
		model.add(treeNodeInfo.convertToStatements());
		return model;
	}

	public List<String> getTreeNodeIdsInRelations() {
		return treeNodeInfo.getTreeNodeIdsInRelations();
	}
}
