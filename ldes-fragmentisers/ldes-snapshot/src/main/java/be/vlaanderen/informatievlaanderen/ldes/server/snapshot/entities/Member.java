package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public record Member(String id, Model model, String versionOf, LocalDateTime timestamp) {
}
