package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_TIMESTAMP_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_VERSION_OF;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_SHAPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_VIEW;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;

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
		List<Statement> statements = new ArrayList<>();
		Resource collectionResource = createResource(ldesConfig.getHostName() + "/" + eventStream.collection());
		statements.add(createStatement(collectionResource, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
		addStatementIfMeaningful(statements, collectionResource, TREE_SHAPE, eventStream.shape());
		addStatementIfMeaningful(statements, collectionResource, LDES_VERSION_OF, eventStream.versionOf());
		addStatementIfMeaningful(statements, collectionResource, LDES_TIMESTAMP_PATH, eventStream.timestampPath());
		eventStream.views().forEach(view -> addStatementIfMeaningful(statements, collectionResource, TREE_VIEW,
				ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + "/" + view));
		model.add(statements);
		return prefixAdder.addPrefixesToModel(model);
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
