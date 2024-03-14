package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import org.apache.jena.rdf.model.Model;

public interface MemberIngester {
    boolean ingest(String collectionName, Model ingestedModel);

}
