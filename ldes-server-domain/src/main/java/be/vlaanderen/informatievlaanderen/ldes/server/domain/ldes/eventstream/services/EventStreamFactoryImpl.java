package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventStreamFactoryImpl implements EventStreamFactory {

	private final LdesConfig ldesConfig;
	private final ViewConfig viewConfig;
	private final TreeNodeFetcher treeNodeFetcher;

	public EventStreamFactoryImpl(LdesConfig ldesConfig, ViewConfig viewConfig, TreeNodeFetcher treeNodeFetcher) {
		this.ldesConfig = ldesConfig;
		this.viewConfig = viewConfig;
		this.treeNodeFetcher = treeNodeFetcher;
	}

	@Override
	public EventStream getEventStream() {
		return new EventStream(ldesConfig.getCollectionName(), ldesConfig.getTimestampPath(),
				ldesConfig.getVersionOfPath(), ldesConfig.validation().getShape(), getViews());
	}

	private List<TreeNode> getViews() {
		return viewConfig
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(viewName -> LdesFragmentRequest.createViewRequest("collectionName", viewName))
				.map(treeNodeFetcher::getFragment)
				.toList();
	}
}
