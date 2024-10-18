package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators;

import org.apache.jena.rdf.model.Model;

public interface IngestValidator {
    void validate(Model model, String collectionName);
}
