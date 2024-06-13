package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface VersionObjectCreator {
    Model createFromMember(IngestedMember member);
}
