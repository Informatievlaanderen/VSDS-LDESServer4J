package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfigDeprecated;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
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
	public EventStream getEventStream(LdesSpecification ldesSpecification) {
		return new EventStream(ldesSpecification.getCollectionName(), ldesSpecification.getTimestampPath(),
				ldesSpecification.getVersionOfPath(), ldesSpecification.validation().getShape(), getViews(ldesSpecification));
	}

	private List<TreeNode> getViews(LdesSpecification ldesSpecification) {
		return ldesSpecification
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(viewName -> LdesFragmentRequest.createViewRequest(ldesSpecification.getCollectionName(), viewName))
				.map(treeNodeFetcher::getFragment)
				.toList();
	}
}
