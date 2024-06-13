package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;

import java.util.List;

@FunctionalInterface
public interface MemberExtractor {
    List<IngestedMember> extractMembers(Model ingestedModel);
}
