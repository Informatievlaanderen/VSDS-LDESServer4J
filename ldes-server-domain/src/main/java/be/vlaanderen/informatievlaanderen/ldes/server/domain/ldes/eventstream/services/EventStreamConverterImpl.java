package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.services.RelationStatementConverter;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class EventStreamConverterImpl implements EventStreamConverter {

	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;
	private final RelationStatementConverter relationStatementConverter;

	public EventStreamConverterImpl(PrefixAdder prefixAdder, LdesConfig ldesConfig,
			RelationStatementConverter relationStatementConverter) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
		this.relationStatementConverter = relationStatementConverter;
	}

	public Model toModel(final EventStream eventStream) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addCollectionStatements(eventStream));
		model.add(addViewStatements(eventStream.views()));
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addCollectionStatements(EventStream eventStream) {
		List<Statement> statements = new ArrayList<>();
		Resource collection = createResource(ldesConfig.getHostName() + "/" + eventStream.collection());
		statements.add(createStatement(collection, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
		addStatementIfMeaningful(statements, collection, TREE_SHAPE, eventStream.shape());
		addStatementIfMeaningful(statements, collection, LDES_VERSION_OF, eventStream.versionOfPath());
		addStatementIfMeaningful(statements, collection, LDES_TIMESTAMP_PATH, eventStream.timestampPath());
		eventStream.views().forEach(view -> addStatementIfMeaningful(statements, collection, TREE_VIEW,
				ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + view.getFragmentId()));
		return statements;
	}

	private List<Statement> addViewStatements(List<TreeNode> views) {
		final List<Statement> statements = new ArrayList<>();

		views.forEach(view -> {
			Resource viewResource = createResource(
					ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + view.getFragmentId());

			statements.add(createStatement(viewResource, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
			statements.addAll(relationStatementConverter.getRelationStatements(view.getRelations(), viewResource));
		});

		return statements;
	}

	private void addStatementIfMeaningful(List<Statement> statements, Resource subject, Property predicate,
			String objectContent) {
		if (hasMeaningfulValue(objectContent))
			statements.add(createStatement(subject, predicate, createResource(objectContent)));
	}

	private boolean hasMeaningfulValue(String objectContent) {
		return objectContent != null && !objectContent.equals("");
	}

}
