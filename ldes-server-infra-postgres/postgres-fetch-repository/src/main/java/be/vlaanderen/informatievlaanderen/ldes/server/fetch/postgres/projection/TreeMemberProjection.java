package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.projection;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public record TreeMemberProjection (String subject, Model model, String versionOf, LocalDateTime timestamp) {
}
