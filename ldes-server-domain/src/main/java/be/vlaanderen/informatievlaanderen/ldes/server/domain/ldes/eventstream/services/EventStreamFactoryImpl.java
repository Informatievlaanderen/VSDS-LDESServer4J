package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfigDeprecated;
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

	// TODO: 12/04/2023 remove ldesconfig and viewconfig
	private final LdesConfigDeprecated ldesConfig;
	private final ViewConfig viewConfig;
	private final TreeNodeFetcher treeNodeFetcher;

	public EventStreamFactoryImpl(LdesConfigDeprecated ldesConfig, ViewConfig viewConfig,
			TreeNodeFetcher treeNodeFetcher) {
		this.ldesConfig = ldesConfig;
		this.viewConfig = viewConfig;
		this.treeNodeFetcher = treeNodeFetcher;
	}

	@Override
	public EventStream getEventStream() {
		return new EventStream(ldesConfig.getCollectionName(), ldesConfig.getTimestampPath(),
				ldesConfig.getVersionOfPath(), ldesConfig.validation().getShape(), getViews());
	}

	// TODO: 12/04/2023 use injected spec part of VSDSPUB-607
	private List<TreeNode> getViews() {
		return viewConfig
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(name -> LdesFragmentRequest.createViewRequest("TODO", name))
				.map(treeNodeFetcher::getFragment)
				.toList();
	}
}
