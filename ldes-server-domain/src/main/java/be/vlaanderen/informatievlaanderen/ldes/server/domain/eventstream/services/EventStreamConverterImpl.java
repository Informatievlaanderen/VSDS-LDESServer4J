package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
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

	public EventStreamConverterImpl(PrefixAdder prefixAdder, LdesConfig ldesConfig) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
	}

	public Model toModel(final EventStream eventStream) {
		Model model = ModelFactory.createDefaultModel();
		List<Statement> statements = new ArrayList<>();
		Resource collectionResource = createResource(ldesConfig.getHostName() + "/" + eventStream.getCollection());
		statements.add(createStatement(collectionResource, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
		addStatementIfMeaningful(statements, collectionResource, TREE_SHAPE, eventStream.getShape());
		addStatementIfMeaningful(statements, collectionResource, LDES_VERSION_OF, eventStream.getVersionOf());
		addStatementIfMeaningful(statements, collectionResource, LDES_TIMESTAMP_PATH, eventStream.getTimestampPath());
		eventStream.getViews().forEach(view -> addStatementIfMeaningful(statements, collectionResource, TREE_VIEW,
				ldesConfig.getHostName() + "/" + view));
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
