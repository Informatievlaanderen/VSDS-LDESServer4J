package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeFetcherImpl implements TreeNodeFetcher {
	private final AppConfig appConfig;
	private final TreeNodeFactory treeNodeFactory;

	public TreeNodeFetcherImpl(AppConfig appConfig, TreeNodeFactory treeNodeFactory) {
		this.appConfig = appConfig;
		this.treeNodeFactory = treeNodeFactory;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		LdesConfig ldesConfig = appConfig.getLdesConfig(ldesFragmentRequest.collectionName());

		TreeNode treeNode = treeNodeFactory
				.getTreeNode(new LdesFragment(
						ldesFragmentRequest.collectionName(), ldesFragmentRequest.viewName(),
						ldesFragmentRequest.fragmentPairs())
						.getFragmentId(), ldesConfig);
		if (treeNode.isSoftDeleted())
			throw new DeletedFragmentException(
					ldesConfig.getHostName()
							+ new LdesFragment(ldesFragmentRequest.collectionName(), ldesFragmentRequest.viewName(),
									ldesFragmentRequest.fragmentPairs()).getFragmentId());
		return treeNode;
	}
}
