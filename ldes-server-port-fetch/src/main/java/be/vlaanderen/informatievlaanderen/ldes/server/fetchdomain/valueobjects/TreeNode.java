package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;

public class TreeNode {
	private final TreeNodeInfo treeNodeInfo;
	private final TreeMemberList treeMemberList;

	public TreeNode(TreeNodeInfo treeNodeInfo, TreeMemberList treeMemberList) {
		this.treeNodeInfo = treeNodeInfo;
		this.treeMemberList = treeMemberList;
	}

	public Model getModel() {
		Model model = ModelFactory.createDefaultModel();
		model.add(treeNodeInfo.convertToStatements());
		model.add(treeMemberList.convertToStatements());
		return model;
	}

	public List<String> getTreeNodeIdsInRelations() {
		return treeNodeInfo.getTreeNodeIdsInRelations();
	}
}
