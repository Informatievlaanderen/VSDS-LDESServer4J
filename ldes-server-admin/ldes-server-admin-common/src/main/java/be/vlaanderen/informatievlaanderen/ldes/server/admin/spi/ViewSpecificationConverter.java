package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.UriPrefixConstructor;
import org.apache.jena.graph.GraphMemFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView.VIEW_DESCRIPTION_SUFFIX;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class ViewSpecificationConverter {

	public static final int DEFAULT_PAGE_SIZE = 100;
	private final RetentionModelExtractor retentionModelExtractor;
	private final FragmentationConfigExtractor fragmentationConfigExtractor;
	private final UriPrefixConstructor prefixConstructor;

	public ViewSpecificationConverter(RetentionModelExtractor retentionModelExtractor,
									  FragmentationConfigExtractor fragmentationConfigExtractor,
									  UriPrefixConstructor prefixConstructor) {
		this.retentionModelExtractor = retentionModelExtractor;
		this.fragmentationConfigExtractor = fragmentationConfigExtractor;
		this.prefixConstructor = prefixConstructor;
	}

	public ViewSpecification viewFromModel(Model viewModel, String collectionName) {
		List<Statement> statements = viewModel.listStatements().toList();
		ViewName viewName = viewNameFromStatements(statements, collectionName);
		int pageSize = pageSizeFromStatements(statements);
		List<Model> retentionPolicies = retentionModelExtractor.extractRetentionStatements(viewModel);
		var fragmentationConfigs = fragmentationConfigExtractor.extractFragmentationConfigs(statements);
		return new ViewSpecification(viewName, retentionPolicies, fragmentationConfigs, pageSize);
	}

	private int pageSizeFromStatements(List<Statement> statements) {
		return statements.stream()
				.filter(statement -> statement.getPredicate().toString().equals(TREE_PAGESIZE))
				.map(statement -> statement.getObject().asLiteral().getInt())
				.findFirst()
				.orElse(DEFAULT_PAGE_SIZE);
	}

	public Model modelFromView(ViewSpecification view) {
		String prefix = prefixConstructor.buildPrefix();
		Model model = ModelFactory.createDefaultModel();
		ViewName viewName = view.getName();
		Resource viewResource = getIRIFromViewName(viewName, prefix);
		Statement viewDescription = createStatement(
				viewResource,
				createProperty(TREE_VIEW_DESCRIPTION),
				getIRIDescription(viewName, prefix));
		model.add(viewDescription);
		model.add(createStatement(viewDescription.getResource(), createProperty(TREE_PAGESIZE),
				createTypedLiteral(view.getPageSize())));
		model.add(createStatement(viewResource, RDF.type, createResource(TREE_NODE_RESOURCE)));

		addRetentionPoliciesToModel(view.getRetentionConfigs(), model, viewDescription);
		model.add(viewDescription.getResource(), RDF.type, createProperty(TREE_VIEW_DESCRIPTION_RESOURCE));
		model.add(extractDcatStatements(view, prefix));
		model.add(fragmentationStatementsFromList(viewDescription.getResource(), view.getFragmentations()));

		return model;
	}

	private void addRetentionPoliciesToModel(List<Model> retentionModels, Model model, Statement viewDescription) {
		retentionModels.forEach(retentionModel -> {
			Resource retentionResource = createResource();
			List<Statement> statements = new ArrayList<>();
			retentionModel.listStatements().forEach(statement -> statements
					.add(createStatement(retentionResource, statement.getPredicate(), statement.getObject())));
			statements.add(
					createStatement(viewDescription.getResource(), createProperty(RETENTION_TYPE), retentionResource));
			model.add(statements);
		});
	}

	private List<Statement> extractDcatStatements(ViewSpecification view, String prefix) {
		return view.getDcat() != null ? view.getDcat().getStatementsWithBase(prefix) : List.of();
	}

	private String getIRIString(ViewName viewName, String prefix) {
		return prefix + "/" + viewName.asString();
	}

	public Resource getIRIFromViewName(ViewName viewName, String prefix) {
		return createResource(getIRIString(viewName, prefix));
	}

	private Resource getIRIDescription(ViewName viewName, String prefix) {
		return createResource(getIRIString(viewName, prefix) + VIEW_DESCRIPTION_SUFFIX);
	}

	private ViewName viewNameFromStatements(List<Statement> statements, String collectionName) {
		String nameString = statements.stream()
				.filter(statement -> statement.getPredicate().toString().equals(TREE_VIEW_DESCRIPTION))
				.map(statement -> statement.getSubject().getLocalName()).findFirst()
				.orElseThrow(() -> new ModelToViewConverterException("Missing type: " + TREE_VIEW_DESCRIPTION));

		return new ViewName(collectionName, nameString);
	}

	private List<Statement> fragmentationStatementsFromList(Resource viewName,
															List<FragmentationConfig> fragmentationList) {
		List<Statement> statements = new ArrayList<>();
		List<ResourceImpl> fragmentationResources = fragmentationList.stream().map(fragmentation -> {
			Node blankNode = NodeFactory.createBlankNode();
			ResourceImpl resource = new ResourceImpl(blankNode, new ModelCom(GraphMemFactory.createGraphMem()));
			resource.addProperty(RDF_SYNTAX_TYPE, createResource(TREE + fragmentation.getName()));
			fragmentation.getConfig().forEach(
					(key, value) -> resource.addProperty(createProperty(TREE + key), createPlainLiteral(value)));
			return resource;
		}).toList();
		RDFList list = ModelFactory.createDefaultModel().createList(fragmentationResources.listIterator());
		statements.add(createStatement(viewName, createProperty(FRAGMENTATION_OBJECT), list));
		list.iterator().forEach(rdfNode -> statements.addAll(rdfNode.getModel().listStatements().toList()));
		fragmentationResources.forEach(resource -> statements.addAll(resource.getModel().listStatements().toList()));
		return statements;
	}

}
