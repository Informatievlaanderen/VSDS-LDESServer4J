package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;

@Component
public class TreeNodeFetcherImpl implements TreeNodeFetcher {
	private final String hostName;
	private final TreeNodeFactory treeNodeFactory;
	private final Boolean useRelativeUrl;

	public TreeNodeFetcherImpl(@Value(HOST_NAME_KEY) String hostName, TreeNodeFactory treeNodeFactory, @Value(USE_RELATIVE_URL_KEY) Boolean useRelativeUrl) {
		this.hostName = Boolean.TRUE.equals(useRelativeUrl) ? ".." : hostName;
		this.treeNodeFactory = treeNodeFactory;
		this.useRelativeUrl = useRelativeUrl;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		final ViewName viewName = ldesFragmentRequest.viewName();
		return treeNodeFactory
				.getTreeNode(new Fragment(
						new LdesFragmentIdentifier(viewName, ldesFragmentRequest.fragmentPairs())).getFragmentId(),
						hostName, viewName.getCollectionName());
	}
}
