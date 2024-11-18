package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators;

import org.apache.jena.rdf.model.Model;

public interface IngestValidator {
    void validate(Model model, String collectionName);
}
