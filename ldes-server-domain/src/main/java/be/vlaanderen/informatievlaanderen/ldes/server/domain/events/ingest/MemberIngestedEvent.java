package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest;

import org.apache.jena.rdf.model.Model;

public record MemberIngestedEvent(Model model, String id, String collectionName, long sequenceNr) {
}
