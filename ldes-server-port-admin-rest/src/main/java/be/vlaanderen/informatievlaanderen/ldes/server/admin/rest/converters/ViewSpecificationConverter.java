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

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.constants.ViewSpecificationConverterConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

public class ViewSpecificationConverter {

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
		String viewName = view.getName().getViewName();
		Statement viewDescription = createStatement(createResource(viewName), createProperty(VIEW_TYPE_OBJECT),
				createResource());
		model.add(viewDescription);
		model.add(retentionStatementsFromList(viewDescription.getResource(), view.getRetentionConfigs()));
		model.add(fragmentationStatementsFromList(viewDescription.getResource(), view.getFragmentations()));

		return model;
	}

	private static ViewName viewNameFromStatements(List<Statement> statements, String collectionName) {
		String nameString = statements.stream()
				.filter(statement -> statement.getPredicate().toString().equals(VIEW_TYPE_OBJECT))
				.map(statement -> statement.getSubject().toString()).findFirst()
				.orElseThrow(() -> new ModelToViewConverterException("Missing type: " + VIEW_TYPE_OBJECT));

		return new ViewName(collectionName, nameString);
	}

	private static List<RetentionConfig> retentionListFromStatements(List<Statement> statements) {
		List<RetentionConfig> retentionList = new ArrayList<>();
		for (Resource retention : statements.stream()
				.filter(new ConfigFilterPredicate(RETENTION_TYPE))
				.map(Statement::getSubject).toList()) {
			List<Statement> retentionStatements = retrieveAllStatements(retention, statements);
			RetentionConfig config = new RetentionConfig();
			config.setName(retention.toString());
			config.setConfig(extractConfigMap(retentionStatements));
			retentionList.add(config);
		}
		return retentionList;
	}

	private static List<Statement> retentionStatementsFromList(Resource viewName, List<RetentionConfig> retentionList) {
		List<Statement> statements = new ArrayList<>();
		for (RetentionConfig retention : retentionList) {
			Resource retentionResource = createResource();
			statements.add(createStatement(
					retentionResource, createProperty(TYPE_PREDICATE), createResource(RETENTION_TYPE)));
			retention.getConfig().forEach((key, value) -> statements.add(createStatement(
					retentionResource, createProperty(key), createPlainLiteral(value))));
			statements.add(createStatement(viewName, createProperty(RETENTION_OBJECT), retentionResource));
		}
		return statements;
	}

	private static List<FragmentationConfig> fragmentationListFromStatements(List<Statement> statements) {
		List<FragmentationConfig> fragmentationList = new ArrayList<>();
		for (Resource fragmentation : statements.stream()
				.filter(new ConfigFilterPredicate(FRAGMENTATION_TYPE))
				.map(Statement::getSubject).toList()) {
			List<Statement> fragmentationStatements = retrieveAllStatements(fragmentation, statements);
			FragmentationConfig config = new FragmentationConfig();
			config.setName(fragmentation.toString());
			config.setConfig(extractConfigMap(fragmentationStatements));
			fragmentationList.add(config);
		}

		return fragmentationList;
	}

	private static List<Statement> fragmentationStatementsFromList(Resource viewName,
			List<FragmentationConfig> fragmentationList) {
		List<Statement> statements = new ArrayList<>();
		for (FragmentationConfig fragmentation : fragmentationList) {
			Resource fragmentationResource = createResource();
			statements.add(createStatement(
					fragmentationResource, createProperty(TYPE_PREDICATE), createResource(FRAGMENTATION_TYPE)));
			fragmentation.getConfig().forEach((key, value) -> statements.add(createStatement(
					fragmentationResource, createProperty(key), createPlainLiteral(value))));
			statements.add(createStatement(viewName, createProperty(FRAGMENTATION_OBJECT), fragmentationResource));
		}
		return statements;
	}

	private static List<Statement> retrieveAllStatements(Resource resource, List<Statement> statements) {
		List<Statement> statementList = new ArrayList<>();
		statements.stream()
				.filter(statement -> statement.getSubject().equals(resource))
				.forEach(statement -> {
					statementList.add(statement);
					if (statement.getObject().isResource()) {
						statementList.addAll(retrieveAllStatements(statement.getResource(), statements));
					}
				});
		return statementList;
	}

	private static Map<String, String> extractConfigMap(List<Statement> statementList) {
		Map<String, String> configMap = new HashMap<>();
		statementList.stream()
				.filter(statement -> !statement.getPredicate().toString().equals(TYPE_PREDICATE))
				.forEach(statement -> configMap.put(
						statement.getPredicate().toString(),
						statement.getObject().asLiteral().getString()));
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
