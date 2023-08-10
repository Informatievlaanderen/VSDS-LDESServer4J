package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class EventStream {
	private final String eventStreamIdentifier;
	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;

	public EventStream(String eventStreamIdentifier, String collection, String timestampPath, String versionOfPath) {
		this.eventStreamIdentifier = eventStreamIdentifier;
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
	}

	public String getCollection() {
		return collection;
	}

	public List<Statement> convertToStatements(String view) {
		List<Statement> statements = new ArrayList<>();
		Resource eventStream = createResource(eventStreamIdentifier);
		statements.add(createStatement(eventStream, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
		addStatementIfMeaningful(statements, eventStream, LDES_VERSION_OF, versionOfPath);
		addStatementIfMeaningful(statements, eventStream, LDES_TIMESTAMP_PATH, timestampPath);
		addStatementIfMeaningful(statements, eventStream, TREE_VIEW, view);
		return Collections.unmodifiableList(statements);
	}

	private void addStatementIfMeaningful(List<Statement> statements, Resource subject, Property predicate,
			String objectContent) {
		if (hasMeaningfulValue(objectContent)) {
			statements.add(createStatement(subject, predicate, createResource(objectContent)));
		}
	}

	private boolean hasMeaningfulValue(String objectContent) {
		return objectContent != null && !objectContent.equals("");
	}

}
