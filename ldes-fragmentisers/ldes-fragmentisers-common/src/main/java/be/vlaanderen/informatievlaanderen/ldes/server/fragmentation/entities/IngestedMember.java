package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public record IngestedMember(long memberId, String subject, LocalDateTime timestamp, String versionOf, Model model) {
}
