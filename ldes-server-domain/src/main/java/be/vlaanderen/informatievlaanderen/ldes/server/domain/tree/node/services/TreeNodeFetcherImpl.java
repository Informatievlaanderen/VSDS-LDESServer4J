package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfigDeprecated;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.CollectionNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
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
		LdesSpecification ldesSpecification = ldesConfig.getLdesSpecification(ldesFragmentRequest.collectionName())
				.orElseThrow(() -> new CollectionNotFoundException(ldesFragmentRequest.collectionName()));

		TreeNode treeNode = treeNodeFactory
				.getTreeNode(new LdesFragment(
						ldesFragmentRequest.collectionName(), ldesFragmentRequest.viewName(),
						ldesFragmentRequest.fragmentPairs())
						.getFragmentId(), ldesSpecification);
		if (treeNode.isSoftDeleted())
			throw new DeletedFragmentException(
					ldesSpecification.getHostName()
							+ new LdesFragment(ldesFragmentRequest.collectionName(), ldesFragmentRequest.viewName(),
									ldesFragmentRequest.fragmentPairs()).getFragmentId());
		return treeNode;
	}
}
