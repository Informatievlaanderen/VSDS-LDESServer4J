package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import org.apache.jena.rdf.model.Model;

public record Member(String id, Model model, Long sequenceNr) {
}
