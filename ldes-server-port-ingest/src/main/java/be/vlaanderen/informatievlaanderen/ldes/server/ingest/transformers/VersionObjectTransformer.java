package be.vlaanderen.informatievlaanderen.ldes.server.ingest.transformers;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface VersionObjectTransformer {
    List<Member> transformToVersionObjects(Model ingestedModel);
}
