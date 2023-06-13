package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.graph.Factory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView.VIEW_DESCRIPTION_SUFFIX;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class ViewSpecificationConverter {

	private final String hostname;
	private final RetentionModelExtractor retentionModelExtractor;
	private final FragmentationConfigExtractor fragmentationConfigExtractor;

	public ViewSpecificationConverter(@Value("${ldes-server.host-name}") String hostName,
			RetentionModelExtractor retentionModelExtractor,
			FragmentationConfigExtractor fragmentationConfigExtractor) {
		this.hostname = hostName;
		this.retentionModelExtractor = retentionModelExtractor;
		this.fragmentationConfigExtractor = fragmentationConfigExtractor;
	}

	public ViewSpecification viewFromModel(Model viewModel, String collectionName) {
		List<Statement> statements = viewModel.listStatements().toList();
		ViewSpecification view = new ViewSpecification();

		view.setName(viewNameFromStatements(statements, collectionName));
		view.setCollectionName(collectionName);
		view.setRetentionPolicies(retentionModelExtractor.extractRetentionStatements(viewModel));
		view.setFragmentations(fragmentationConfigExtractor.extractFragmentationConfigs(statements));
		return view;
	}

	public Model modelFromView(ViewSpecification view) {
		Model model = ModelFactory.createDefaultModel();
		ViewName viewName = view.getName();
		Resource viewResource = getIRIFromViewName(viewName);
		Statement viewDescription = createStatement(
				viewResource,
				createProperty(TREE_VIEW_DESCRIPTION),
				getIRIDescription(viewName));
		model.add(viewDescription);
		model.add(createStatement(viewResource, RDF.type, createResource(TREE_NODE_RESOURCE)));

		addRetentionPoliciesToModel(view.getRetentionConfigs(), model, viewDescription);
		model.add(viewDescription.getResource(), RDF.type, createProperty(TREE_VIEW_DESCRIPTION_RESOURCE));
		model.add(extractDcatStatements(view));
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

	private List<Statement> extractDcatStatements(ViewSpecification view) {
		return view.getDcat() != null ? view.getDcat().getStatementsWithBase(hostname) : List.of();
	}

	private String getIRIString(ViewName viewName) {
		return hostname + "/" + viewName.getCollectionName() + "/" + viewName.getViewName();
	}

	public Resource getIRIFromViewName(ViewName viewName) {
		return createResource(getIRIString(viewName));
	}

	private Resource getIRIDescription(ViewName viewName) {
		return createResource(getIRIString(viewName) + VIEW_DESCRIPTION_SUFFIX);
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
			ResourceImpl resource = new ResourceImpl(blankNode, new ModelCom(Factory.createGraphMem()));
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
