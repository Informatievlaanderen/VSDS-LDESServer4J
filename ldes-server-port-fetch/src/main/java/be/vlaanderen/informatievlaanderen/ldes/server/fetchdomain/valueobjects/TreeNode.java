package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;

public class TreeNode {
	private final TreeNodeInfo treeNodeInfo;
	private final TreeMembers treeMembers;

	public TreeNode(TreeNodeInfo treeNodeInfo, TreeMembers treeMembers) {
		this.treeNodeInfo = treeNodeInfo;
		this.treeMembers = treeMembers;
	}

	public Model getModel() {
		Model model = ModelFactory.createDefaultModel();
		model.add(treeNodeInfo.convertToStatements());
		model.add(treeMembers.convertToStatements());
		return model;
	}

	public List<String> getTreeNodeIdsInRelations() {
		return treeNodeInfo.getTreeNodeIdsInRelations();
	}
}
