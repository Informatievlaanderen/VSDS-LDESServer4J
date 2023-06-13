package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeFetcherImpl implements TreeNodeFetcher {
	private String hostName;
	private final TreeNodeFactory treeNodeFactory;

	public TreeNodeFetcherImpl(@Value("${ldes-server.host-name}") String hostName, TreeNodeFactory treeNodeFactory) {
		this.hostName = hostName;
		this.treeNodeFactory = treeNodeFactory;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		final ViewName viewName = ldesFragmentRequest.viewName();
		return treeNodeFactory
				.getTreeNode(new LdesFragment(
						viewName,
						ldesFragmentRequest.fragmentPairs())
						.getFragmentId(), hostName, viewName.getCollectionName());
	}
}
