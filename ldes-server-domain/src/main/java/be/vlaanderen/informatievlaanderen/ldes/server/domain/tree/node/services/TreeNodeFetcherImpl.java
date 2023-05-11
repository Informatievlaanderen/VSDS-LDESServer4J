package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
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
		final ViewName viewName = ldesFragmentRequest.viewName();
		return treeNodeFactory
				.getTreeNode(new LdesFragment(
						viewName,
						ldesFragmentRequest.fragmentPairs())
						.getFragmentId(), appConfig.getHostName(), viewName.getCollectionName());
	}
}
