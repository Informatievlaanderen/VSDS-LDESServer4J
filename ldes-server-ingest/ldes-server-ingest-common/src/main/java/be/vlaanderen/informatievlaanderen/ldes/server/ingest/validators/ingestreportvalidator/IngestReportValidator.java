package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface IngestReportValidator {
    void validate(Model model, EventStream eventStream, ShaclReportManager reportManager);
}
