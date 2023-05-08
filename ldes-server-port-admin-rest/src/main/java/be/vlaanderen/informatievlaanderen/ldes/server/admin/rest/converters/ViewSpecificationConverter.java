package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions.ModelToViewConverterException;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.constants.ViewSpecificationConverterConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class ViewSpecificationConverter {

	public static final String FRAGMENTATION_NAME = "name";
	public static final String RETENTION_NAME = "name";
	private final String hostname;

	public ViewSpecificationConverter(AppConfig appConfig) {
		if (appConfig.getCollections().size() == 1) {
			LdesConfig ldesConfig = appConfig.getCollections().get(0);
			hostname = ldesConfig.getHostName();
		} else {
			//todo update code when appconfig is reworked
			hostname = "undefined";
		}
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
		Statement viewDescription = createStatement(createResource(hostname + viewName.getCollectionName() + "/" + viewName.getViewName()),
				createProperty(VIEW_TYPE_OBJECT),
				createResource());
		model.add(viewDescription);
		model.add(retentionStatementsFromList(viewDescription.getResource(), view.getRetentionConfigs()));
		model.add(fragmentationStatementsFromList(viewDescription.getResource(), view.getFragmentations()));

		return model;
	}

	private ViewName viewNameFromStatements(List<Statement> statements, String collectionName) {
		String nameString = statements.stream()
				.filter(statement -> statement.getPredicate().toString().equals(VIEW_TYPE_OBJECT))
				.map(statement -> statement.getSubject().getLocalName()).findFirst()
				.orElseThrow(() -> new ModelToViewConverterException("Missing type: " + VIEW_TYPE_OBJECT));

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
					retentionResource, createProperty(TYPE_PREDICATE), createResource(RETENTION_TYPE)));
			retention.getConfig().forEach((key, value) -> statements.add(createStatement(
					retentionResource, createProperty(CUSTOM + key), createPlainLiteral(value))));
			statements.add(createStatement(retentionResource, createProperty(CUSTOM + FRAGMENTATION_NAME),
					createPlainLiteral(retention.getName())));
			statements.add(createStatement(viewName, createProperty(RETENTION_OBJECT), retentionResource));
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
					fragmentationResource, createProperty(TYPE_PREDICATE), createResource(FRAGMENTATION_TYPE)));
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
				.filter(statement -> !statement.getPredicate().toString().equals(TYPE_PREDICATE))
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
			return statement.getPredicate().toString().equals(TYPE_PREDICATE)
					&& statement.getObject().toString().equals(type);
		}
	}
}
