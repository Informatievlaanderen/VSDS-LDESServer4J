package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface EventStreamServiceSpi {
	List<EventStreamResponse> retrieveAllEventStreams();

	EventStreamResponse retrieveEventStream(String collectionName);

	Model getComposedDcat();
}
