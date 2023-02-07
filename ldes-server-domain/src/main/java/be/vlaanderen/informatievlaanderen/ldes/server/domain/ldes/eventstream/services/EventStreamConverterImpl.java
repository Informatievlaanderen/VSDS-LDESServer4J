package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeRelationResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventStreamConverterImpl implements EventStreamConverter {

	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;

	public EventStreamConverterImpl(PrefixAdder prefixAdder, LdesConfig ldesConfig) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
	}

	public Model toModel(final EventStream eventStream) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addCollectionStatements(eventStream));
		model.add(addViewStatements(eventStream.views()));
		model.add(ldesConfig.getDcat());
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addCollectionStatements(EventStream eventStream) {
		String eventStreamId = ldesConfig.getHostName() + "/" + eventStream.collection();
		List<String> views = eventStream.views().stream().map(TreeNode::getFragmentId)
				.map(s -> ldesConfig.getHostName() + "/" + eventStream.collection() + s).toList();
		EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(eventStreamId,
				eventStream.timestampPath(), eventStream.versionOfPath(), eventStream.shape(), views);
		return eventStreamInfoResponse.convertToStatements();
	}

	private List<Statement> addViewStatements(List<TreeNode> views) {
		final List<Statement> statements = new ArrayList<>();

		views.forEach(view -> {
			String treeNodeId = ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + view.getFragmentId();
			List<TreeRelationResponse> treeRelationResponses = view.getRelations().stream()
					.map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
							treeRelation.treeNode(),
							ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName()
									+ treeRelation.treeValue(),
							treeRelation.treeValueType(), treeRelation.relation()))
					.toList();
			TreeNodeInfoResponse treeNodeInfoResponse = new TreeNodeInfoResponse(treeNodeId, treeRelationResponses);
			statements.addAll(treeNodeInfoResponse.convertToStatements());
		});

		return statements;
	}

}
