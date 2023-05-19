package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class EventStreamResponseConverterImpl implements EventStreamResponseConverter {

	public static final String CUSTOM = "http://example.org/";
	public static final Property MEMBER_TYPE = createProperty(CUSTOM, "memberType");
	private final String hostname;
	private final ViewSpecificationConverter viewSpecificationConverter;
	private final PrefixAdder prefixAdder;

	public EventStreamResponseConverterImpl(AppConfig appConfig, ViewSpecificationConverter viewSpecificationConverter,
											PrefixAdder prefixAdder) {
		this.viewSpecificationConverter = viewSpecificationConverter;
		hostname = appConfig.getHostName();
		this.prefixAdder = prefixAdder;
	}

	@Override
	public EventStreamResponse fromModel(Model model) {
		final String collection = getIdentifier(model, createResource(EVENT_STREAM_TYPE)).map(Resource::getLocalName)
				.orElseThrow();
		final String timestampPath = getResource(model, LDES_TIMESTAMP_PATH);
		final String versionOfPath = getResource(model, LDES_VERSION_OF);
		final List<ViewSpecification> views = getViews(model, collection);
		final String memberType = getResource(model, MEMBER_TYPE);
		final Model shacl = getShaclFromModel(model);
		return new EventStreamResponse(collection, timestampPath, versionOfPath, memberType, views, shacl);
	}

	@Override
	public Model toModel(EventStreamResponse eventStreamResponse) {
		final Resource subject = getIRIFromCollectionName(eventStreamResponse.getCollection());
		final Statement collectionNameStmt = createStatement(subject, RDF_SYNTAX_TYPE,
				createResource(EVENT_STREAM_TYPE));
		final Statement timestampPathStmt = createStatement(subject, LDES_TIMESTAMP_PATH,
				createProperty(eventStreamResponse.getTimestampPath()));
		final Statement versionOfStmt = createStatement(subject, LDES_VERSION_OF,
				createProperty(eventStreamResponse.getVersionOfPath()));
		final Statement memberType = createStatement(subject, MEMBER_TYPE,
				createProperty(eventStreamResponse.getMemberType()));

		Model eventStreamModel = createDefaultModel()
				.add(List.of(collectionNameStmt, timestampPathStmt, versionOfStmt, memberType))
				.add(eventStreamResponse.getShacl())
				.add(getShaclReferenceStatement(eventStreamResponse.getShacl(), subject))
				.add(getViewReferenceStatements(eventStreamResponse.getViews(), subject))
				.add(getViewStatements(eventStreamResponse.getViews()));

		return prefixAdder.addPrefixesToModel(eventStreamModel);
	}

	private List<Statement> getViewReferenceStatements(List<ViewSpecification> views, Resource subject) {
		return views.stream()
				.map(ViewSpecification::getName)
				.map(viewName -> createStatement(subject, createProperty(VIEW), viewSpecificationConverter.getIRIFromViewName(viewName)))
				.toList();
	}

	private List<Statement> getViewStatements(List<ViewSpecification> views) {
		return views.stream()
				.map(viewSpecificationConverter::modelFromView)
				.flatMap(model -> model.listStatements().toList().stream())
				.toList();
	}

	private Statement getShaclReferenceStatement(Model shacl, Resource subject) {
		return getIdentifier(shacl, createResource(NODE_SHAPE_TYPE))
				.map(resource -> createStatement(subject, TREE_SHAPE, resource))
				.orElse(null);
	}

	private Resource getIRIFromCollectionName(String name) {
		return createResource(hostname + "/" + name);
	}

	private Optional<Resource> getIdentifier(Model model, Resource object) {
		Optional<Statement> stmtOptional = model.listStatements(null, RDF_SYNTAX_TYPE, object).nextOptional();
		return stmtOptional.map(Statement::getSubject);
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
				.map(viewModel -> viewSpecificationConverter.viewFromModel(viewModel, collection))
				.toList();
	}

	/**
	 * @param resource the resource of which the according statements need to be
	 *                 retrieved
	 * @param model    the model of which all the statements need to be retrieved
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
