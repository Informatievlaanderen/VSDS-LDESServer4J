package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public interface TreeMemberProjection {
	String getSubject();
	Model getModel();
	String getVersionOf();
	LocalDateTime getTimestamp();
}
