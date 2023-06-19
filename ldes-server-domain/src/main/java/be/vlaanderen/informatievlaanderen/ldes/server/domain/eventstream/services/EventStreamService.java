package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface EventStreamService {
	List<EventStreamResponse> retrieveAllEventStreams();

	EventStreamResponse retrieveEventStream(String collectionName);

	String retrieveMemberType(String collectionName);

	void deleteEventStream(String collectionName);

	EventStreamResponse createEventStream(EventStreamResponse eventStream);

	Model getComposedDcat();

}
