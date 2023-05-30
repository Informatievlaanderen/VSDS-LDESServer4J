package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatNodeValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatValidator.*;

public class CannotContainServiceValidator implements DcatNodeValidator {
	@Override
	public void validate(Model dcat) {
		if (dcat.listSubjectsWithProperty(RDF.type, DCAT_DATA_SERVICE).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a data service.");
		}

		if (dcat.listSubjectsWithProperty(DCAT_DATA_SERVICE_PREDICATE).hasNext()) {
			throw new IllegalArgumentException("Model cannot contain a relation to the data service.");
		}

	}
}
