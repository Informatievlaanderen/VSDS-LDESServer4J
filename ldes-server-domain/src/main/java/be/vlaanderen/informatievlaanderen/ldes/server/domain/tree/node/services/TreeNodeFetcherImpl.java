package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeFetcherImpl implements TreeNodeFetcher {
	private final LdesConfig ldesConfig;
	private final TreeNodeFactory treeNodeFactory;

	public TreeNodeFetcherImpl(LdesConfig ldesConfig, TreeNodeFactory treeNodeFactory) {
		this.ldesConfig = ldesConfig;
		this.treeNodeFactory = treeNodeFactory;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		TreeNode treeNode = treeNodeFactory
				.getTreeNode(new LdesFragment(
						ldesFragmentRequest.viewName(), ldesFragmentRequest.fragmentPairs())
						.getFragmentId());
		if (treeNode.isSoftDeleted())
			throw new DeletedFragmentException(
					ldesConfig.getHostName() + new LdesFragment(ldesFragmentRequest.viewName(),
							ldesFragmentRequest.fragmentPairs()).getFragmentId());
		return treeNode;
	}
}
