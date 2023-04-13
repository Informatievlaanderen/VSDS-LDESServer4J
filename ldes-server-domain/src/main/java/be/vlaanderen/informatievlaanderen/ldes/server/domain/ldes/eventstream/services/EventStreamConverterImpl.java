package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfigDeprecated;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeRelationResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
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
		LdesSpecification ldesSpecification = ldesConfig.getLdesSpecification(eventStream.collection()).orElseThrow();

		Model model = ModelFactory.createDefaultModel();
		model.add(addCollectionStatements(eventStream, ldesSpecification));
		model.add(addViewStatements(eventStream.views(), ldesSpecification));
		model.add(ldesSpecification.getDcat());
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addCollectionStatements(EventStream eventStream, LdesSpecification ldesSpecification) {
		String eventStreamId = ldesSpecification.getHostName() + "/" + eventStream.collection();
		List<String> views = eventStream.views().stream().map(TreeNode::getFragmentId).toList();
		EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(eventStreamId,
				eventStream.timestampPath(), eventStream.versionOfPath(), eventStream.shape(), views);
		return eventStreamInfoResponse.convertToStatements();
	}

	private List<Statement> addViewStatements(List<TreeNode> views, LdesSpecification ldesSpecification) {
		final List<Statement> statements = new ArrayList<>();

		views.forEach(view -> {
			List<TreeRelationResponse> treeRelationResponses = view.getRelations().stream()
					.map(treeRelation -> new TreeRelationResponse(treeRelation.treePath(),
							ldesSpecification.getBaseUrl()
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
