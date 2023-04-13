package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeRelationResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventStreamConverterImpl implements EventStreamConverter {

	private final PrefixAdder prefixAdder;
	private final AppConfig appConfig;

	public EventStreamConverterImpl(PrefixAdder prefixAdder, AppConfig appConfig) {
		this.prefixAdder = prefixAdder;
		this.appConfig = appConfig;
	}

	public Model toModel(final EventStream eventStream) {
		LdesConfig ldesConfig = appConfig.getLdesConfig(eventStream.collection());

		Model model = ModelFactory.createDefaultModel();
		model.add(addCollectionStatements(eventStream, ldesConfig));
		model.add(addViewStatements(eventStream.views(), ldesConfig));
		model.add(ldesConfig.getDcat());
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addCollectionStatements(EventStream eventStream, LdesConfig ldesConfig) {
		String eventStreamId = ldesConfig.getHostName() + "/" + eventStream.collection();
		List<String> views = eventStream.views().stream().map(TreeNode::getFragmentId).toList();
		EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(eventStreamId,
				eventStream.timestampPath(), eventStream.versionOfPath(), eventStream.shape(), views);
		return eventStreamInfoResponse.convertToStatements();
	}

	private List<Statement> addViewStatements(List<TreeNode> views, LdesConfig ldesConfig) {
		final List<Statement> statements = new ArrayList<>();

		views.forEach(view -> {
			List<TreeRelationResponse> treeRelationResponses = view.getRelations().stream()
					.map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
							ldesConfig.getBaseUrl()
									+ treeRelation.treeNode(),
							treeRelation.treeValue(),
							treeRelation.treeValueType(), treeRelation.relation()))
					.toList();
			TreeNodeInfoResponse treeNodeInfoResponse = new TreeNodeInfoResponse(view.getFragmentId(),
					treeRelationResponses);
			statements.addAll(treeNodeInfoResponse.convertToStatements());

		});

		return statements;
	}

}
