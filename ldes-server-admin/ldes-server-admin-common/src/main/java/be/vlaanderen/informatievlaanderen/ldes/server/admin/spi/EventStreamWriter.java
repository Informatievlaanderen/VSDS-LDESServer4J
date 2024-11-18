package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.HostNamePrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.UriPrefixConstructor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class EventStreamWriter {
	private static final String DCAT_PREFIX = "http://www.w3.org/ns/dcat#";
	private static final String DATASET_TYPE = DCAT_PREFIX + "Dataset";

	private final ViewSpecificationConverter viewSpecificationConverter;
	private final PrefixAdder prefixAdder;
	private final UriPrefixConstructor prefixConstructor;

	public EventStreamWriter(ViewSpecificationConverter viewSpecificationConverter, PrefixAdder prefixAdder, UriPrefixConstructor prefixConstructor) {
		this.viewSpecificationConverter = viewSpecificationConverter;
		this.prefixAdder = prefixAdder;
		this.prefixConstructor = prefixConstructor;
	}

	public Model write(EventStreamTO eventStreamTO) {
		String prefix = prefixConstructor.buildPrefix();
		final Resource subject = getIRIFromCollectionName(eventStreamTO.getCollection(), prefix);
		final Statement collectionNameStmt = createStatement(subject, RDF_SYNTAX_TYPE, createResource(EVENT_STREAM_TYPE));
		final Statement dcatTypeStmt = createStatement(subject, RDF_SYNTAX_TYPE, createResource(DATASET_TYPE));
		final Model dataset = eventStreamTO.getDcatDataset().getModelWithIdentity(prefix);
		final List<Statement> eventSourceStatements = getEventSourceStatements(eventStreamTO.getEventSourceRetentionPolicies(), subject);

		Model eventStreamModel = createDefaultModel()
				.add(collectionNameStmt)
				.add(dcatTypeStmt)
				.add(getVersionOfStatements(subject, eventStreamTO))
				.add(getTimestampPathStatements(subject, eventStreamTO))
				.add(getCreateVersionsStatements(subject, eventStreamTO))
				.add(eventStreamTO.getShacl())
				.add(getViewReferenceStatements(eventStreamTO.getViews(), subject, prefix))
				.add(getViewStatements(eventStreamTO.getViews()))
				.add(eventSourceStatements)
				.add(dataset);

		if(prefixConstructor instanceof HostNamePrefixConstructor) {
			eventStreamModel.setNsPrefix(eventStreamTO.getCollection(), prefixConstructor.buildPrefix() + "/" + eventStreamTO.getCollection() + "/");
		}

		Statement shaclStatement = getShaclReferenceStatement(eventStreamTO.getShacl(), subject);

		eventStreamModel.add(shaclStatement);

		return prefixAdder.addPrefixesToModel(eventStreamModel);
	}

	private List<Statement> getTimestampPathStatements(Resource subject, EventStreamTO eventStreamTO) {
		if (isNotBlank(eventStreamTO.getVersionOfPath())) {
			return List.of(createStatement(subject, LDES_TIMESTAMP_PATH,
					createProperty(eventStreamTO.getTimestampPath())));
		} else {
			return List.of();
		}
	}

	private List<Statement> getVersionOfStatements(Resource subject, EventStreamTO eventStreamTO) {
		if (isNotBlank(eventStreamTO.getVersionOfPath())) {
			return List.of(createStatement(subject, LDES_VERSION_OF,
					createProperty(eventStreamTO.getVersionOfPath())));
		} else {
			return List.of();
		}
	}

	private List<Statement> getCreateVersionsStatements(Resource subject, EventStreamTO eventStreamTO) {
		return List.of(
				createStatement(subject, LDES_CREATE_VERSIONS, createTypedLiteral(eventStreamTO.isVersionCreationEnabled()))
		);
	}

	private List<Statement> getViewReferenceStatements(List<ViewSpecification> views, Resource subject, String prefix) {
		return views.stream()
				.map(ViewSpecification::getName)
				.map(viewName -> createStatement(subject, createProperty(VIEW),
						viewSpecificationConverter.getIRIFromViewName(viewName, prefix)))
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

	private List<Statement> getEventSourceStatements(List<Model> eventSourceRetentionModels, Resource subject) {
		List<Statement> statements = new ArrayList<>();
		Resource eventSourceResource = createResource();
		statements.add(
				createStatement(subject, LDES_EVENT_SOURCE, eventSourceResource));
		statements.add(
				createStatement(eventSourceResource, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_SOURCE_URI)));
		eventSourceRetentionModels.forEach(retentionModel -> {
			Resource retentionResource = createResource();
			retentionModel.listStatements().forEach(statement -> statements
					.add(createStatement(retentionResource, statement.getPredicate(), statement.getObject())));
			statements.add(
					createStatement(eventSourceResource, createProperty(RETENTION_TYPE), retentionResource));
		});
		return statements;
	}

	private Resource getIRIFromCollectionName(String name, String prefix) {
		return createResource(prefix + "/" + name);
	}

	private Optional<Resource> getIdentifier(Model model, Resource object) {
		Optional<Statement> stmtOptional = model.listStatements(null, RDF_SYNTAX_TYPE, object).nextOptional();
		return stmtOptional.map(Statement::getSubject);
	}
}
