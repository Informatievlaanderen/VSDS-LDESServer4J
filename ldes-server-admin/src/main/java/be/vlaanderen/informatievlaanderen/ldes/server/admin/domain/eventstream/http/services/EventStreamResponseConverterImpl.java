package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingStatementException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.HOST_NAME_KEY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.apache.jena.shacl.vocabulary.SHACL.targetClass;

@Component
public class EventStreamResponseConverterImpl implements EventStreamResponseConverter {

	public static final String DCAT_PREFIX = "http://www.w3.org/ns/dcat#";
	public static final String DATASET_TYPE = DCAT_PREFIX + "Dataset";
	private final String hostname;
	private final ViewSpecificationConverter viewSpecificationConverter;
	private final PrefixAdder prefixAdder;

	public EventStreamResponseConverterImpl(@Value(HOST_NAME_KEY) String hostName,
			ViewSpecificationConverter viewSpecificationConverter,
			PrefixAdder prefixAdder) {
		this.viewSpecificationConverter = viewSpecificationConverter;
		this.hostname = hostName;
		this.prefixAdder = prefixAdder;
	}

	@Override
	public EventStreamResponse fromModel(Model model) {
		final String collection = getIdentifier(model, createResource(EVENT_STREAM_TYPE)).map(Resource::getLocalName)
				.orElseThrow(() -> new MissingStatementException("Not blank node with type " + EVENT_STREAM_TYPE));
		final String timestampPath = getResource(model, LDES_TIMESTAMP_PATH);
		final String versionOfPath = getResource(model, LDES_VERSION_OF);
		final List<ViewSpecification> views = getViews(model, collection);
		final Model shacl = getShaclFromModel(model);
		final String memberType = extractMemberType(shacl);
		return new EventStreamResponse(collection, timestampPath, versionOfPath, memberType, views,
				shacl);
	}

	@Override
	public Model toModel(EventStreamResponse eventStreamResponse) {
		final Resource subject = getIRIFromCollectionName(eventStreamResponse.getCollection());
		final Statement collectionNameStmt = createStatement(subject, RDF_SYNTAX_TYPE,
				createResource(EVENT_STREAM_TYPE));
		final Model dataset = eventStreamResponse.getDcatDataset().getModelWithIdentity(hostname);

		Model eventStreamModel = createDefaultModel()
				.add(collectionNameStmt)
				.add(getVersionOfStatements(subject, eventStreamResponse))
				.add(getTimestampPathStatements(subject, eventStreamResponse))
				.add(eventStreamResponse.getShacl())
				.add(getViewReferenceStatements(eventStreamResponse.getViews(), subject))
				.add(getViewStatements(eventStreamResponse.getViews()))
				.add(dataset);

		Statement shaclStatement = getShaclReferenceStatement(eventStreamResponse.getShacl(), subject);

		eventStreamModel.add(shaclStatement);
		eventStreamModel.add(createStatement(shaclStatement.getResource(), createProperty(targetClass.getURI()),
				createProperty(eventStreamResponse.getMemberType())));

		return prefixAdder.addPrefixesToModel(eventStreamModel);
	}

	private List<Statement> getTimestampPathStatements(Resource subject, EventStreamResponse eventStreamResponse) {
		if (isNotBlank(eventStreamResponse.getVersionOfPath())) {
			return List.of(createStatement(subject, LDES_TIMESTAMP_PATH,
					createProperty(eventStreamResponse.getTimestampPath())));
		} else {
			return List.of();
		}
	}

	private List<Statement> getVersionOfStatements(Resource subject, EventStreamResponse eventStreamResponse) {
		if (isNotBlank(eventStreamResponse.getVersionOfPath())) {
			return List.of(createStatement(subject, LDES_VERSION_OF,
					createProperty(eventStreamResponse.getVersionOfPath())));
		} else {
			return List.of();
		}
	}

	private List<Statement> getViewReferenceStatements(List<ViewSpecification> views, Resource subject) {
		return views.stream()
				.map(ViewSpecification::getName)
				.map(viewName -> createStatement(subject, createProperty(VIEW),
						viewSpecificationConverter.getIRIFromViewName(viewName)))
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
				.orElse(createStatement(subject, TREE_MEMBER, createResource()));
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

	private String extractMemberType(Model shacl) {
		return shacl.listObjectsOfProperty(null, shacl.createProperty(targetClass.getURI()))
				.nextOptional()
				.map(RDFNode::toString)
				.orElseThrow(() -> {
					throw new IllegalArgumentException(
							"Could not find a targetClass in provided shape. Missing statement: "
									+ targetClass.getURI());
				});
	}
}
