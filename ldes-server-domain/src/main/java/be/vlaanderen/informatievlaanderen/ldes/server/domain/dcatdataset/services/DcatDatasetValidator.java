package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

@Component
public class DcatDatasetValidator {
	public static final String DCAT_PREFIX = "http://www.w3.org/ns/dcat#";
	public static final String CATALOG_TYPE = DCAT_PREFIX + "DataCatalog";
	public static final String SERVICE_TYPE = DCAT_PREFIX + "DataService";
	public static final String DATASET_TYPE = DCAT_PREFIX + "Dataset";

	public void validate(DcatDataset dataset) {
		Model datasetModel = dataset.model();
		doesNotContainType(datasetModel, CATALOG_TYPE);
		doesNotContainType(datasetModel, SERVICE_TYPE);
		datasetModel.listStatements(null, RDF_SYNTAX_TYPE, createResource(DATASET_TYPE)).forEach(statement -> {
			if (!statement.getSubject().isAnon()) {
				throw new RuntimeException("Node of type " + DATASET_TYPE + " must be blank node.");
			}
		});
	}

	private void doesNotContainType(Model model, String type) {
		// todo: custom exception, possibly same as in view validation
		model.listStatements(null, RDF_SYNTAX_TYPE, createResource(type)).forEach(statement -> {
			throw new RuntimeException("Entity of type " + type + " is of not allowed.");
		});
		model.listStatements(null, createProperty(type), (Resource) null).forEach(statement -> {
			throw new RuntimeException(
					"cannot contain relation to entity of type " + type);
		});
	}
}
