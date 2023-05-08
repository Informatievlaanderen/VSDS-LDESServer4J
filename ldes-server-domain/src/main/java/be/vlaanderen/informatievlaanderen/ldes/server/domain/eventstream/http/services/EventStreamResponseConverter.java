package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.EVENT_STREAM_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_TIMESTAMP_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_VERSION_OF;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.NODE_SHAPE_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_SHAPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.VIEW;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class EventStreamResponseConverter {

	public static final String CUSTOM = "http://example.org/";
	public static final Property MEMBER_TYPE = createProperty(CUSTOM, "memberType");

	public EventStreamResponse fromModel(Model model) {
		final String collection = getIdentifier(model, createResource(EVENT_STREAM_TYPE)).replace(LDES, "");
		final String timestampPath = getResource(model, LDES_TIMESTAMP_PATH);
		final String versionOfPath = getResource(model, LDES_VERSION_OF);
		final List<ViewSpecification> views = getViews(model, collection);
		final String memberType = getResource(model, MEMBER_TYPE);
		final Model shacl = getShaclFromModel(model);
		return new EventStreamResponse(collection, timestampPath, versionOfPath, memberType, views, shacl);
	}

	public Model toModel(EventStreamResponse eventStreamResponse) {
		final Resource subject = createResource(LDES + eventStreamResponse.getCollection());
		final Statement collectionNameStmt = createStatement(subject, RDF_SYNTAX_TYPE,
				createResource(EVENT_STREAM_TYPE));
		final Statement timestampPathStmt = createStatement(subject, LDES_TIMESTAMP_PATH,
				createProperty(eventStreamResponse.getTimestampPath()));
		final Statement versionOfStmt = createStatement(subject, LDES_VERSION_OF,
				createProperty(eventStreamResponse.getVersionOfPath()));
		final Statement memberType = createStatement(subject, MEMBER_TYPE,
				createProperty(eventStreamResponse.getMemberType()));

		final List<Statement> views = eventStreamResponse.getViews().stream()
				.map(ViewSpecificationConverter::modelFromView)
				.flatMap(model -> model.listStatements().toList().stream())
				.toList();

		final Resource shaclResource = createResource(
				getIdentifier(eventStreamResponse.getShacl(), createResource(NODE_SHAPE_TYPE)));
		final Statement shaclStmt = createStatement(subject, TREE_SHAPE, shaclResource);

		final List<Statement> viewReferenceStatements = eventStreamResponse.getViews().stream()
				.map(view -> createStatement(subject, createProperty(VIEW),
						createProperty(view.getName().getViewName())))
				.toList();

		return createDefaultModel()
				.add(List.of(collectionNameStmt, timestampPathStmt, versionOfStmt, memberType, shaclStmt))
				.add(views)
				.add(viewReferenceStatements)
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

	private List<ViewSpecification> getViews(Model model, String collection) {
		return model.listStatements(null, createProperty(TREE_VIEW_DESCRIPTION), (Resource) null).toList().stream()
				.map(statement -> {
					List<Statement> statements = retrieveAllStatements((Resource) statement.getObject(), model);
					statements.add(statement);
					return statements;
				})
				.map(statements -> createDefaultModel().add(statements))
				.map(viewModel -> ViewSpecificationConverter.viewFromModel(viewModel, collection))
				.toList();
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
