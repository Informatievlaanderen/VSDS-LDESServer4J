package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators;

import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface CannotContainRule {
	boolean evaluate(Model model);
}
