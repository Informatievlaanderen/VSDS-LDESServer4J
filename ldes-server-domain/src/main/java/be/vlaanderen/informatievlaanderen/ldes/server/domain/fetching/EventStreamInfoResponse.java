package be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class EventStreamInfoResponse {

	private final String eventStreamId;
	private final String timestampPath;
	private final String versionOfPath;
	private final String shape;
	private final List<String> views;

	public EventStreamInfoResponse(String eventStreamId, String timestampPath, String versionOfPath, String shape,
			List<String> views) {
		this.eventStreamId = eventStreamId;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.shape = shape;
		this.views = views;
	}

	public List<Statement> convertToStatements() {
		List<Statement> statements = new ArrayList<>();
		Resource collection = createResource(eventStreamId);
		statements.add(createStatement(collection, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
		addStatementIfMeaningful(statements, collection, TREE_SHAPE, shape);
		addStatementIfMeaningful(statements, collection, LDES_VERSION_OF, versionOfPath);
		addStatementIfMeaningful(statements, collection, LDES_TIMESTAMP_PATH, timestampPath);
		views.forEach(view -> addStatementIfMeaningful(statements, collection, TREE_VIEW, view));
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
