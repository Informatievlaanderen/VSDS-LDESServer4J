package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import org.apache.jena.rdf.model.*;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class EventStreamConverter {

	public EventStream fromModel(Model model) {
		String collectionName = getCollectionName(model);
		String timestampPath = getTimestampPath(model);
		String versionOfPath = getVersionOfPath(model);
		return new EventStream(collectionName, timestampPath, versionOfPath, List.of());
	}

	public Model toModel(EventStream eventStream) {
		Model model = ModelFactory.createDefaultModel();
		Statement collectionNameStmt = model.createStatement(createSubject(eventStream), RDF_SYNTAX_TYPE, createResource(EVENT_STREAM_TYPE));
		Statement timestampPathStmt = model.createStatement(createSubject(eventStream), LDES_TIMESTAMP_PATH, eventStream.getTimestampPath());
		Statement versionOfStmt = model.createStatement(createSubject(eventStream), LDES_VERSION_OF, eventStream.getVersionOfPath());
		model.add(List.of(collectionNameStmt, timestampPathStmt, versionOfStmt));

		return model;
	}

	private Resource createSubject(EventStream eventStream) {
		return createResource(LDES + eventStream.getCollection());
	}

	private String getCollectionName(Model model) {
		Optional<Statement> stmtOptional = model.listStatements(null, RDF_SYNTAX_TYPE, createResource(EVENT_STREAM_TYPE)).nextOptional();
		return stmtOptional.map(statement -> statement.getSubject().toString().replace(LDES, "")).orElseThrow();
	}

	private String getVersionOfPath(Model model) {
		Optional<Statement> stmtOptional = model.listStatements(null, LDES_VERSION_OF, (Resource) null).nextOptional();
		return stmtOptional.map(statement -> statement.getObject().toString()).orElse(null);
	}

	private String getTimestampPath(Model model) {
		Optional<Statement> stmtOptional = model.listStatements(null, LDES_TIMESTAMP_PATH, (Resource) null).nextOptional();
		return stmtOptional.map(statement -> statement.getObject().toString()).orElse(null);
	}
}
