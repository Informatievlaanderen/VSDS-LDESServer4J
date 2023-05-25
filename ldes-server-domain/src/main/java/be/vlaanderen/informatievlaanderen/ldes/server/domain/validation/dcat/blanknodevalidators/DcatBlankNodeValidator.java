package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators;

import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface DcatBlankNodeValidator {
	void validateBlankNode(Model dcat);
}
