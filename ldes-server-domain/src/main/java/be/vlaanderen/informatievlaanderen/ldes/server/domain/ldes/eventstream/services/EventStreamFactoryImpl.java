package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventStreamFactoryImpl implements EventStreamFactory {

	private final TreeNodeFetcher treeNodeFetcher;

	public EventStreamFactoryImpl(TreeNodeFetcher treeNodeFetcher) {
		this.treeNodeFetcher = treeNodeFetcher;
	}

	@Override
	public EventStream getEventStream(LdesConfig ldesConfig) {
		return new EventStream(ldesConfig.getCollectionName(), ldesConfig.getTimestampPath(),
				ldesConfig.getVersionOfPath(), ldesConfig.validation().getShape(),
				getViews(ldesConfig));
	}

	private List<TreeNode> getViews(LdesConfig ldesConfig) {
		return ldesConfig
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(LdesFragmentRequest::createViewRequest)
				.map(treeNodeFetcher::getFragment)
				.toList();
	}

}
