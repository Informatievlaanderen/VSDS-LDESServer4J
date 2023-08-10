package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;

public class TreeNode {
	private final EventStreamInfo eventStreamInfo;
	private final TreeNodeInfo treeNodeInfo;
	private final TreeMemberList treeMemberList;

	public TreeNode(EventStreamInfo eventStreamInfo, TreeNodeInfo treeNodeInfo, TreeMemberList treeMemberList) {
		this.eventStreamInfo = eventStreamInfo;
		this.treeNodeInfo = treeNodeInfo;
		this.treeMemberList = treeMemberList;
	}

	public Model getModel() {
		Model model = ModelFactory.createDefaultModel();
		model.add(eventStreamInfo.convertToStatements());
		model.add(treeNodeInfo.convertToStatements());
		model.add(treeMemberList.convertToStatements());
		return model;
	}

	public List<String> getTreeNodeIdsInRelations() {
		return treeNodeInfo.getTreeNodeIdsInRelations();
	}

	public List<String> getMemberIds() {
		return treeMemberList.getMemberIds();
	}
}
