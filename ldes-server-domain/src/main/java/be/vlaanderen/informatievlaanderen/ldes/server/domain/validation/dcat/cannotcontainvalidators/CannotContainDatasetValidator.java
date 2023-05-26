package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatValidator.*;

public class CannotContainDatasetValidator implements CannotContainValidator {
	@Override
	public void validate(Model dcat) {
		if (dcat.listSubjectsWithProperty(DCAT_SERVES_DATASET).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a relation to the dataset.");
		}

		if (dcat.listSubjectsWithProperty(RDF.type, DCAT_DATASET).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a dataset.");
		}

		if (dcat.listSubjectsWithProperty(DCAT_DATASET_PREDICATE).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a relation to the dataset.");
		}
	}
}
