package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

@FunctionalInterface
public interface VersionObjectCreator {
	Model createFromMember(String subject, Model model, String versionOf, LocalDateTime timestamp);
}
