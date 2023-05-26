package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatValidator.DCAT_DATA_SERVICE;

public class DcatBlankDataServiceNode implements DcatBlankNodeValidator {
	@Override
	public void validateBlankNode(Model dcat) {
		final List<Resource> resources = dcat.listSubjectsWithProperty(RDF.type, DCAT_DATA_SERVICE).toList();
		if (resources.size() != 1) {
			throw new IllegalArgumentException("Model must include exactly one DataService. Not more, not less.");
		}

		if (!resources.get(0).asNode().isBlank()) {
			throw new IllegalArgumentException("Node of type dcat:DataService must be a blank node");
		}
	}
}
