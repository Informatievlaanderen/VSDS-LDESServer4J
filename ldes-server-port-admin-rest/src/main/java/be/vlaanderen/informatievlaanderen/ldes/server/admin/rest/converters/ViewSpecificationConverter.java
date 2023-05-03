package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.apache.jena.rdf.model.ResourceFactory.*;

public class ViewSpecificationConverter {

    static final String TYPE_PREDICATE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    static final String RETENTION_TYPE_OBJECT = "https://w3id.org/ldes#retentionPolicy";
    static final String FRAGMENTATION_TYPE_OBJECT = "http://example.org/Fragmentation";
    static final String VIEW_TYPE_OBJECT = "https://w3id.org/tree#viewDescription";

    private ViewSpecificationConverter() {
    }

    public static ViewSpecification viewFromModel(Model viewModel, String collectionName) {
        List<Statement> statements = viewModel.listStatements().toList();
        ViewSpecification view = new ViewSpecification();

        view.setName(viewNameFromStatements(statements, collectionName));
        view.setCollectionName(collectionName);
        view.setRetentionPolicies(retentionListFromStatements(statements));
        view.setFragmentations(fragmentationListFromStatements(statements));

        return view;
    }

    public static Model modelFromView(ViewSpecification view) {
        Model model = ModelFactory.createDefaultModel();
        String viewName = view.getName().asString();
        model.add(createStatement(createResource(viewName), createProperty(VIEW_TYPE_OBJECT), createResource()));
        model.add(retentionStatementsFromList(viewName, view.getRetentionConfigs()));
        model.add(fragmentationStatementsFromList(viewName, view.getFragmentations()));
        return model;
    }

    private static ViewName viewNameFromStatements(List<Statement> statements, String collectionName) {
        String nameString = statements.stream().filter(statement -> statement.getPredicate().toString().equals(VIEW_TYPE_OBJECT))
                .map(statement -> statement.getSubject().toString()).findFirst().orElseThrow(() -> new ModelToViewConverterException("Missing type: " + VIEW_TYPE_OBJECT));

        return new ViewName(collectionName, nameString);
    }

    private static List<RetentionConfig> retentionListFromStatements(List<Statement> statements) {
        List<RetentionConfig> retentionList = new ArrayList<>();
        for (Resource retention : statements.stream().filter(new ConfigFilterPredicate(RETENTION_TYPE_OBJECT)).map(Statement::getSubject).toList()) {
            List<Statement> retentionStatements = retrieveAllStatements(retention, statements);
            RetentionConfig config = new RetentionConfig();
            config.setName(retention.toString());
            config.setConfig(extractConfigMap(retentionStatements));
            retentionList.add(config);
        }
        return retentionList;
    }

    private static List<Statement> retentionStatementsFromList(String viewName, List<RetentionConfig> retentionList) {
        List<Statement> statements = new ArrayList<>();
        for (RetentionConfig retention : retentionList) {
            statements.add(createStatement(
                    createResource(retention.getName()), createProperty(TYPE_PREDICATE), createResource(RETENTION_TYPE_OBJECT)));
            retention.getConfig().forEach((key, value) -> statements.add(createStatement(
                        createResource(retention.getName()), createProperty(key), createPlainLiteral(value))));
            statements.add(createStatement(createResource(viewName), createProperty(RETENTION_TYPE_OBJECT), createResource(retention.getName())));
        }
        return statements;
    }

    private static List<FragmentationConfig> fragmentationListFromStatements(List<Statement> statements) {
        List<FragmentationConfig> fragmentationList = new ArrayList<>();
        for (Resource fragmentation : statements.stream().filter(new ConfigFilterPredicate(FRAGMENTATION_TYPE_OBJECT)).map(Statement::getSubject).toList()) {
            List<Statement> fragmentationStatements = retrieveAllStatements(fragmentation, statements);
            FragmentationConfig config = new FragmentationConfig();
            config.setName(fragmentation.toString());
            config.setConfig(extractConfigMap(fragmentationStatements));
            fragmentationList.add(config);
        }

        return fragmentationList;
    }

    private static List<Statement> fragmentationStatementsFromList(String viewName, List<FragmentationConfig> fragmentationList) {
        List<Statement> statements = new ArrayList<>();
        for (FragmentationConfig fragmentation : fragmentationList) {
            statements.add(createStatement(
                    createResource(fragmentation.getName()), createProperty(TYPE_PREDICATE), createResource(FRAGMENTATION_TYPE_OBJECT)));
            fragmentation.getConfig().forEach((key, value) -> statements.add(createStatement(
                        createResource(fragmentation.getName()), createProperty(key), createPlainLiteral(value))));
            statements.add(createStatement(createResource(viewName), createProperty(FRAGMENTATION_TYPE_OBJECT), createResource(fragmentation.getName())));
        }
        return statements;
    }

    private static List<Statement> retrieveAllStatements(Resource resource, List<Statement> statements) {
        List<Statement> statementList = new ArrayList<>();
        statements.stream().filter(statement -> statement.getSubject().equals(resource))
                .forEach(statement -> {
                    statementList.add(statement);
                    if(statement.getObject().isResource()) {
                        statementList.addAll(retrieveAllStatements(statement.getResource(), statements));
                    }
                });
        return statementList;
    }

    private static Map<String, String> extractConfigMap(List<Statement> statementList) {
        Map<String, String> configMap = new HashMap<>();
        statementList.stream().filter(statement -> !statement.getPredicate().toString().equals(TYPE_PREDICATE)).forEach(statement -> configMap.put(statement.getPredicate().toString(), statement.getObject().asLiteral().getString()));
        return configMap;
    }

    public static class ConfigFilterPredicate implements Predicate<Statement> {

        private final String type;

        public ConfigFilterPredicate(String type) {
            this.type = type;
        }

        @Override
        public boolean test(Statement statement) {
            return statement.getPredicate().toString().equals(TYPE_PREDICATE)
                    && statement.getObject().toString().equals(type);
        }
    }
}
