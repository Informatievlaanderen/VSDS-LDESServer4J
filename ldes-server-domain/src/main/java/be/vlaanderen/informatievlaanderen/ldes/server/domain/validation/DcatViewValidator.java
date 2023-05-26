package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@Component
public class DcatViewValidator implements Validator {

	public static final Property DCAT_DATA_SERVICE = createProperty("http://www.w3.org/ns/dcat#DataService");
	public static final Property DCAT_DATASET = createProperty("http://www.w3.org/ns/dcat#Dataset");
	public static final Property DCAT_CATALOG = createProperty("http://www.w3.org/ns/dcat#Catalog");
	public static final Property DCAT_SERVES_DATASET = createProperty("http://www.w3.org/ns/dcat#servesDataset");

	@Override
	public void validate(Object target, Errors errors) {
		final Model dcat = (Model) target;
		validate(dcat);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Model.class.isAssignableFrom(clazz);
	}

	private void validate(Model dcat) {
		hasOneBlankDataServiceNode(dcat);
		hasNoDatasetStatements(dcat);
		hasNoCatalogStatements(dcat);
	}

	private void hasOneBlankDataServiceNode(Model dcat) {
		final List<Resource> resources = dcat.listSubjectsWithProperty(RDF.type, DCAT_DATA_SERVICE).toList();
		if (resources.size() != 1) {
			throw new IllegalArgumentException("Model must include exactly one DataService. Not more, not less.");
		}

		if (!resources.get(0).asNode().isBlank()) {
			throw new IllegalArgumentException("dcat:DataService must be a blank node");
		}
	}

	private void hasNoDatasetStatements(Model dcat) {
		if (dcat.listSubjectsWithProperty(DCAT_SERVES_DATASET).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a relation to the dataset.");
		}

		if (dcat.listSubjectsWithProperty(RDF.type, DCAT_DATASET).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a dataset.");
		}
	}

	private void hasNoCatalogStatements(Model dcat) {
		if (dcat.listSubjectsWithProperty(RDF.type, DCAT_CATALOG).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a dataset.");
		}
	}

}
