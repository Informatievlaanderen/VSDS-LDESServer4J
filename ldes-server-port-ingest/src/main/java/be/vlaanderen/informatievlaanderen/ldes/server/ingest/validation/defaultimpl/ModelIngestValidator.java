package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import org.apache.jena.rdf.model.Model;

public interface ModelIngestValidator {

	void validate(Model model);

}
