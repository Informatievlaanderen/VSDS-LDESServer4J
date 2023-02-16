package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeRelationResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE_RESOURCE;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

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
		List<String> views = eventStream.views().stream().map(TreeNode::getFragmentId).toList();
		EventStreamInfoResponse eventStreamInfoResponse = new EventStreamInfoResponse(eventStreamId,
				eventStream.timestampPath(), eventStream.versionOfPath(), eventStream.shape(), views);
		return eventStreamInfoResponse.convertToStatements();
	}

	private List<Statement> addViewStatements(List<TreeNode> views) {
		final List<Statement> statements = new ArrayList<>();

		views.forEach(view -> {
			Resource viewResource = createResource(view.getFragmentId());
			statements.add(createStatement(viewResource, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
			view.getRelations()
					.forEach(treeRelation -> {
						TreeRelationResponse treeRelationResponse = new TreeRelationResponse(treeRelation.treePath(),
								ldesConfig.getBaseUrl()
										+ treeRelation.treeNode(),
								treeRelation.treeValue(),
								treeRelation.treeValueType(), treeRelation.relation());
						statements.addAll(treeRelationResponse.convertToStatements(view.getFragmentId()));
					});

		});

		return statements;
	}

}
