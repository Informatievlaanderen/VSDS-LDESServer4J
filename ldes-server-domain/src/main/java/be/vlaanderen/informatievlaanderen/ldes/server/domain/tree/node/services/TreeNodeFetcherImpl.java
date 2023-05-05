package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
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
	private final EventStreamCollection eventStreamCollection;

	public TreeNodeFetcherImpl(AppConfig appConfig, TreeNodeFactory treeNodeFactory,
			EventStreamCollection eventStreamCollection) {
		this.appConfig = appConfig;
		this.treeNodeFactory = treeNodeFactory;
		this.eventStreamCollection = eventStreamCollection;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		final ViewName viewName = ldesFragmentRequest.viewName();
		EventStream eventStream = eventStreamCollection.retrieveEventStream(viewName.getCollectionName())
				.orElseThrow(() -> new MissingEventStreamException(viewName.getCollectionName()));

		TreeNode treeNode = treeNodeFactory
				.getTreeNode(new LdesFragment(
						viewName,
						ldesFragmentRequest.fragmentPairs())
						.getFragmentId(), appConfig.getHostName(), eventStream.getCollection());
		if (treeNode.isSoftDeleted())
			throw new DeletedFragmentException(
					appConfig.getHostName()
							+ new LdesFragment(viewName,
									ldesFragmentRequest.fragmentPairs()).getFragmentId());
		return treeNode;
	}
}
