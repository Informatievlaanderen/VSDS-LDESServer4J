package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class ViewSpecificationConverter {

	public static final String FRAGMENTATION_NAME = "name";
	public static final String RETENTION_NAME = "name";
	private final String hostname;

	public ViewSpecificationConverter(AppConfig appConfig) {
		hostname = appConfig.getHostName();
	}

	public ViewSpecification viewFromModel(Model viewModel, String collectionName) {
		List<Statement> statements = viewModel.listStatements().toList();
		ViewSpecification view = new ViewSpecification();

		view.setName(viewNameFromStatements(statements, collectionName));
		view.setCollectionName(collectionName);
		view.setRetentionPolicies(retentionListFromStatements(statements));
		view.setFragmentations(fragmentationListFromStatements(statements));

		return view;
	}

	public Model modelFromView(ViewSpecification view) {
		Model model = ModelFactory.createDefaultModel();
		ViewName viewName = view.getName();
		Statement viewDescription = createStatement(
				getIRIFromViewName(viewName),
				createProperty(TREE_VIEW_DESCRIPTION),
				createResource());
		model.add(viewDescription);
		model.add(retentionStatementsFromList(viewDescription.getResource(), view.getRetentionConfigs()));
		model.add(fragmentationStatementsFromList(viewDescription.getResource(), view.getFragmentations()));

		return model;
	}

	public Resource getIRIFromViewName(ViewName viewName) {
		return createResource(hostname + "/" + viewName.getCollectionName() + "/" + viewName.getViewName());
	}

	private ViewName viewNameFromStatements(List<Statement> statements, String collectionName) {
		String nameString = statements.stream()
				.filter(statement -> statement.getPredicate().toString().equals(TREE_VIEW_DESCRIPTION))
				.map(statement -> statement.getSubject().getLocalName()).findFirst()
				.orElseThrow(() -> new ModelToViewConverterException("Missing type: " + TREE_VIEW_DESCRIPTION));

		return new ViewName(collectionName, nameString);
	}

	private List<RetentionConfig> retentionListFromStatements(List<Statement> statements) {
		List<RetentionConfig> retentionList = new ArrayList<>();
		for (Resource retention : statements.stream()
				.filter(new ConfigFilterPredicate(RETENTION_TYPE))
				.map(Statement::getSubject).toList()) {
			List<Statement> retentionStatements = retrieveAllStatements(retention, statements);
			RetentionConfig config = new RetentionConfig();
			Map<String, String> configMap = extractConfigMap(retentionStatements);
			// TODO verify Retention name corresponds with a valid policy
			if (!configMap.containsKey(RETENTION_NAME)) {
				throw new ModelToViewConverterException("Missing retention name");
			}
			config.setName(configMap.remove(RETENTION_NAME));
			config.setConfig(configMap);
			retentionList.add(config);
		}
		return retentionList;
	}

	private List<Statement> retentionStatementsFromList(Resource viewName, List<RetentionConfig> retentionList) {
		List<Statement> statements = new ArrayList<>();
		for (RetentionConfig retention : retentionList) {
			Resource retentionResource = createResource();
			statements.add(createStatement(
					retentionResource, RDF_SYNTAX_TYPE, createResource(RETENTION_TYPE)));
			retention.getConfig().forEach((key, value) -> statements.add(createStatement(
					retentionResource, createProperty(CUSTOM + key), createPlainLiteral(value))));
			statements.add(createStatement(retentionResource, createProperty(CUSTOM + FRAGMENTATION_NAME),
					createPlainLiteral(retention.getName())));
			statements.add(createStatement(viewName, createProperty(RETENTION_TYPE), retentionResource));
		}
		return statements;
	}

	private List<FragmentationConfig> fragmentationListFromStatements(List<Statement> statements) {
		List<FragmentationConfig> fragmentationList = new ArrayList<>();
		for (Resource fragmentation : statements.stream()
				.filter(new ConfigFilterPredicate(FRAGMENTATION_TYPE))
				.map(Statement::getSubject).toList()) {
			List<Statement> fragmentationStatements = retrieveAllStatements(fragmentation, statements);
			FragmentationConfig config = new FragmentationConfig();
			Map<String, String> configMap = extractConfigMap(fragmentationStatements);
			// TODO verify Fragmentation name corresponds with a valid strategy
			if (!configMap.containsKey(FRAGMENTATION_NAME)) {
				throw new ModelToViewConverterException("Missing fragmentation name");
			}
			config.setName(configMap.remove(FRAGMENTATION_NAME));
			config.setConfig(configMap);
			fragmentationList.add(config);
		}

		return fragmentationList;
	}

	private List<Statement> fragmentationStatementsFromList(Resource viewName,
			List<FragmentationConfig> fragmentationList) {
		List<Statement> statements = new ArrayList<>();
		for (FragmentationConfig fragmentation : fragmentationList) {
			Resource fragmentationResource = createResource();
			statements.add(createStatement(
					fragmentationResource, RDF_SYNTAX_TYPE, createResource(FRAGMENTATION_TYPE)));
			fragmentation.getConfig().forEach((key, value) -> statements.add(createStatement(
					fragmentationResource, createProperty(CUSTOM + key), createPlainLiteral(value))));
			statements.add(createStatement(fragmentationResource, createProperty(CUSTOM + FRAGMENTATION_NAME),
					createPlainLiteral(fragmentation.getName())));
			statements.add(createStatement(viewName, createProperty(FRAGMENTATION_OBJECT), fragmentationResource));
		}
		return statements;
	}

	private List<Statement> retrieveAllStatements(Resource resource, List<Statement> statements) {
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

	private Map<String, String> extractConfigMap(List<Statement> statementList) {
		Map<String, String> configMap = new HashMap<>();
		statementList.stream()
				.filter(statement -> !statement.getPredicate().toString().equals(RDF_SYNTAX_TYPE.toString()))
				.forEach(statement -> configMap.put(
						statement.getPredicate().getLocalName(),
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
			return statement.getPredicate().toString().equals(RDF_SYNTAX_TYPE.toString())
					&& statement.getObject().toString().equals(type);
		}
	}
}
