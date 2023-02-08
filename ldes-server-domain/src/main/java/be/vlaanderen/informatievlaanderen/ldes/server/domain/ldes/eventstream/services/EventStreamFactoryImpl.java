package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeRelationResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
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
	public EventStreamResponse getEventStream() {
		return new EventStreamResponse(getEventStreamInfoResponse(), getViews());
	}

	private EventStreamInfoResponse getEventStreamInfoResponse() {
		String eventStreamId = ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName();
		List<String> views = viewConfig
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(LdesFragmentRequest::createViewRequest)
				.map(treeNodeFetcher::getFragment)
				.map(s -> ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + s.getFragmentId()).toList();
		return new EventStreamInfoResponse(eventStreamId,
				ldesConfig.getTimestampPath(), ldesConfig.getVersionOfPath(), ldesConfig.validation().getShape(),
				views);
	}

	private List<TreeNodeInfoResponse> getViews() {
		return viewConfig
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(LdesFragmentRequest::createViewRequest)
				.map(treeNodeFetcher::getFragment)
				.map(view -> {
					String treeNodeId = ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName()
							+ view.getFragmentId();
					List<TreeRelationResponse> treeRelationResponses = view.getRelations().stream()
							.map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
									treeRelation.treeNode(),
									ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName()
											+ treeRelation.treeValue(),
									treeRelation.treeValueType(), treeRelation.relation()))
							.toList();
					return new TreeNodeInfoResponse(treeNodeId, treeRelationResponses);
				})
				.toList();
	}
}
