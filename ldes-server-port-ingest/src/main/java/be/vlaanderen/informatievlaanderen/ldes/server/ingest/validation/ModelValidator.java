package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import org.apache.jena.rdf.model.Model;

public interface ModelValidator {

    void validate(Model model);

}
