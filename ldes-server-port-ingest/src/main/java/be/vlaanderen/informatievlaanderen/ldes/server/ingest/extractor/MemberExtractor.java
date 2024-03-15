package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface MemberExtractor {
    List<Member> extractMembers(Model ingestedModel);
}
