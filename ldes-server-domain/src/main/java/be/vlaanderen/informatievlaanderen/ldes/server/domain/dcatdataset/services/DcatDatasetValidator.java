package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class DcatDatasetValidator implements Validator {
	private static final String CATALOG_TYPE = "dcat:DataCatalog";
	private static final String SERVICE_TYPE = "dcat:DataService";
	private static final String DATASET_TYPE = "dcat:Dataset";

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(DcatDataset.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Model datasetModel = ((DcatDataset) target).model();
		doesNotContainType(datasetModel, CATALOG_TYPE);
		doesNotContainType(datasetModel, SERVICE_TYPE);
		datasetModel.listStatements(null, RDF_SYNTAX_TYPE, DATASET_TYPE).forEach(statement -> {
			if (!statement.getSubject().isAnon()) {
				throw new RuntimeException("Node of type " + DATASET_TYPE + " must be blank node.");
			}
		});

	}

	private void doesNotContainType(Model model, String type) {
		// todo: custom exception, possibly same as in view validation
		model.listStatements(null, RDF_SYNTAX_TYPE, createResource(type)).forEach(statement -> {
			throw new RuntimeException(statement.getSubject().toString() + " is of not allowed type " + type);
		});
		model.listStatements(null, createProperty(type), (Resource) null).forEach(statement -> {
			throw new RuntimeException(
					statement.getSubject().toString() + " cannot contain relation to entity of type " + type);
		});
	}
}
