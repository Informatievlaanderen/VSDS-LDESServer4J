package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions.MissingStatementException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class EventStreamConverterImpl implements EventStreamConverter {

    public static final String DCAT_PREFIX = "http://www.w3.org/ns/dcat#";
    public static final String DATASET_TYPE = DCAT_PREFIX + "Dataset";
    private final ViewSpecificationConverter viewSpecificationConverter;
    private final RetentionModelExtractor retentionModelExtractor;
    private final PrefixAdder prefixAdder;
    private final PrefixConstructor prefixConstructor;

    public EventStreamConverterImpl(ViewSpecificationConverter viewSpecificationConverter, RetentionModelExtractor retentionModelExtractor,
                                    PrefixAdder prefixAdder, PrefixConstructor prefixConstructor) {
        this.viewSpecificationConverter = viewSpecificationConverter;
        this.retentionModelExtractor = retentionModelExtractor;
        this.prefixAdder = prefixAdder;
        this.prefixConstructor = prefixConstructor;
    }

    @Override
    public EventStreamTO fromModel(Model model) {
        final String collection = getIdentifier(model, createResource(EVENT_STREAM_TYPE)).map(Resource::getLocalName)
                .orElseThrow(() -> new MissingStatementException("Not blank node with type " + EVENT_STREAM_TYPE));
        final String timestampPath = getResource(model, LDES_TIMESTAMP_PATH).orElse(null);
        final String versionOfPath = getResource(model, LDES_VERSION_OF).orElse(null);
        final boolean versionCreationEnabled = getBooleanResource(model, LDES_CREATE_VERSIONS).orElse(false);
        final List<ViewSpecification> views = getViews(model, collection);
        final Model shacl = getShaclFromModel(model);
        final List<Model> eventSourceRetentionModels = getEventSourceRetentionPolicies(model);
        return new EventStreamTO(collection, timestampPath, versionOfPath, versionCreationEnabled, views, shacl, eventSourceRetentionModels);
    }

    @Override
    public Model toModel(EventStreamTO eventStreamTO) {
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

    private Optional<String> getResource(Model model, Property predicate) {
        return model.listStatements(null, predicate, (Resource) null)
                .nextOptional()
                .map(statement -> statement.getObject().toString());
    }

    private Optional<Boolean> getBooleanResource(Model model, Property predicate) {
        return model.listStatements(null, predicate, (Resource) null)
                .nextOptional()
                .map(statement -> statement.getObject().asLiteral())
                .map(Literal::getBoolean);
    }

    private Model getShaclFromModel(Model model) {
        final Model shaclModel = ModelFactory.createDefaultModel();
        model.listStatements(null, TREE_SHAPE, (Resource) null)
                .nextOptional()
                .ifPresent(statement -> shaclModel.add(retrieveAllStatements(statement.getResource(), model)));
        return shaclModel;
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

    private List<Model> getEventSourceRetentionPolicies(Model model) {
        Optional<Statement> eventSourceStatement = model.listStatements(null, LDES_EVENT_SOURCE,  (RDFNode) null).nextOptional();
        if (eventSourceStatement.isEmpty()) {
            return List.of();
        } else {
            Model eventSourceModel = ModelFactory.createDefaultModel().add(retrieveAllStatements(eventSourceStatement.get().getResource(), model));
            return retentionModelExtractor.extractRetentionStatements(eventSourceModel);
        }
    }

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
