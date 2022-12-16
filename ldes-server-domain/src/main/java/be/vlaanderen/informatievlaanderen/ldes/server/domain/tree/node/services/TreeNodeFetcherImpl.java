package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
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
				.getTreeNode(new FragmentInfo(ldesFragmentRequest.viewName(), ldesFragmentRequest.fragmentPairs())
						.generateFragmentId());
		if (treeNode.isSoftDeleted())
			throw new DeletedFragmentException(
					ldesConfig.getHostName() + new FragmentInfo(ldesFragmentRequest.viewName(),
							ldesFragmentRequest.fragmentPairs()).generateFragmentId());
		return treeNode;
	}
}
