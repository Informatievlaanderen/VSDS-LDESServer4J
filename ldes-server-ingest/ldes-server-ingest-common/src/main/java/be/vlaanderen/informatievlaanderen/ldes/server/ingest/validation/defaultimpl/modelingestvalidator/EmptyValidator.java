package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator;

import org.apache.jena.rdf.model.Model;

public class EmptyValidator implements ModelIngestValidator {
	@Override
	public void validate(Model model) {
		// Do nothing in the empty validator
	}
}
