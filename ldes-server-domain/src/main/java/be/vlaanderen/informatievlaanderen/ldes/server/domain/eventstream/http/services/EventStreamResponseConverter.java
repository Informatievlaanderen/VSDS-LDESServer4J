package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.*;

public class EventStreamResponseConverter {

	public EventStreamResponse fromModel(Model model) {
		final String collection = getIdentifier(model, createResource(EVENT_STREAM_TYPE)).replace(LDES, "");
		final String timestampPath = getResource(model, LDES_TIMESTAMP_PATH);
		final String versionOfPath = getResource(model, LDES_VERSION_OF);
		final Model views = getViewsFromModel(model);
		final Model shacl = getShaclFromModel(model);
		return new EventStreamResponse(collection, timestampPath, versionOfPath, views, shacl);
	}

	public Model toModel(EventStreamResponse eventStreamResponse) {
		final Resource subject = createResource(LDES + eventStreamResponse.getCollection());
		final Statement collectionNameStmt = createStatement(subject, RDF_SYNTAX_TYPE,
				createResource(EVENT_STREAM_TYPE));
		final Statement timestampPathStmt = createStatement(subject, LDES_TIMESTAMP_PATH,
				createProperty(eventStreamResponse.getTimestampPath()));
		final Statement versionOfStmt = createStatement(subject, LDES_VERSION_OF,
				createProperty(eventStreamResponse.getVersionOfPath()));

		final Resource shaclResource = createResource(
				getIdentifier(eventStreamResponse.getShacl(), createResource(NODE_SHAPE_TYPE)));
		final Statement shaclStmt = createStatement(subject, TREE_SHAPE, shaclResource);

		List<Statement> viewStatements = eventStreamResponse.getViews()
				.listStatements(null, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE))
				.toList().stream()
				.map(statement -> createStatement(subject, createProperty(VIEW), statement.getSubject()))
				.toList();

		return createDefaultModel()
				.add(List.of(collectionNameStmt, timestampPathStmt, versionOfStmt, shaclStmt))
				.add(eventStreamResponse.getViews())
				.add(viewStatements)
				.add(eventStreamResponse.getShacl());
	}

	private String getIdentifier(Model model, Resource object) {
		Optional<Statement> stmtOptional = model.listStatements(null, RDF_SYNTAX_TYPE, object).nextOptional();
		return stmtOptional.map(statement -> statement.getSubject().toString()).orElseThrow();
	}

	private String getResource(Model model, Property predicate) {
		Optional<Statement> stmtOptional = model.listStatements(null, predicate, (Resource) null).nextOptional();
		return stmtOptional.map(statement -> statement.getObject().toString()).orElse(null);
	}

	private Model getShaclFromModel(Model model) {
		final Statement shaclStatement = model.listStatements(null, TREE_SHAPE, (Resource) null).nextStatement();
		List<Statement> shaclStatements = retrieveAllStatements(shaclStatement.getResource(), model);

		return createDefaultModel().add(shaclStatements);
	}

	private Model getViewsFromModel(Model model) {
		Model views = createDefaultModel();
		model.listStatements(null, createProperty(VIEW), (Resource) null)
				.toList().stream()
				.map(Statement::getResource)
				.map(resource -> retrieveAllStatements(resource, model))
				.forEach(views::add);
		return views;
	}

	/**
	 * @param resource
	 *            the resource of which the according statements need to be
	 *            retrieved
	 * @param model
	 *            the model of which all the statements need to be retrieved
	 * @return a list of all the according statement of the model
	 */
	private List<Statement> retrieveAllStatements(Resource resource, Model model) {
		StmtIterator iterator = model.listStatements(resource, null, (Resource) null);
		List<Statement> statements = new ArrayList<>();

		while (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			statements.add(statement);

			if (statement.getObject().isResource()) {
				statements.addAll(retrieveAllStatements(statement.getResource(), model));
			}
		}

		return statements;
	}
}
