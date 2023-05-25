package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class DcatDatasetValidator {
    private static final String DCAT_PREFIX = "http://www.w3.org/ns/dcat#";
    private static final String DCT_PREFIX = "http://purl.org/dc/terms/";
	private static final String CATALOG_TYPE = DCAT_PREFIX + "DataCatalog";
	private static final String SERVICE_TYPE = DCAT_PREFIX + "DataService";
	private static final String DATASET_TYPE = DCAT_PREFIX + "Dataset";
    private static final List<String> ALLOWED_PROPERTIES = List.of(DCT_PREFIX + "title", DCT_PREFIX + "description", DCT_PREFIX + "issued", DCT_PREFIX + "modified",
			DCT_PREFIX + "language", DCT_PREFIX + "publisher", DCT_PREFIX + "accrualPeriodicity", DCT_PREFIX + "identifier", DCT_PREFIX + "spatial", DCT_PREFIX + "temporal", DCT_PREFIX + "theme", DCT_PREFIX + "keyword", DCT_PREFIX + "contactPoint", DCT_PREFIX + "distribution", DCT_PREFIX + "landingPage");


	public void validate(Model datasetModel) {
		containsOnlyDatasetProperties(datasetModel);
		doesNotContainType(datasetModel, CATALOG_TYPE);
		doesNotContainType(datasetModel, SERVICE_TYPE);
		datasetModel.listStatements(null, RDF_SYNTAX_TYPE, DATASET_TYPE).forEach(statement -> {
			if (!statement.getSubject().isAnon()) {
				throw new RuntimeException("Node of type " + DATASET_TYPE + " must be blank node.");
			}
		});

	}

    private void containsOnlyDatasetProperties(Model model) {
        Resource datasetSubjet = model.listStatements(null, RDF_SYNTAX_TYPE, createResource(DATASET_TYPE)).nextStatement().getSubject();
        model.listStatements(datasetSubjet, null, (Resource) null).forEach(statement -> {
			if (!ALLOWED_PROPERTIES.contains(statement.getPredicate().toString())) {
				throw new RuntimeException("Node of type " + DATASET_TYPE + " cannot contain property " + statement.getPredicate().toString());
			}});
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
