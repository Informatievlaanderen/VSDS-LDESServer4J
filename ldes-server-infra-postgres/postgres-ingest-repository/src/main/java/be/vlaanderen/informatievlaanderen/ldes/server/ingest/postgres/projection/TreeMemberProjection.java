package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection;

import org.apache.jena.rdf.model.Model;

public interface TreeMemberProjection {
	String getSubject();
	Model getModel();
}
