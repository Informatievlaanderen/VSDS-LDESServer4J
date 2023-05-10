package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;

import java.util.List;

public interface EventStreamService {
	List<EventStreamResponse> retrieveAllEventStreams();

	EventStreamResponse retrieveEventStream(String collectionName);

	void deleteEventStream(String collectionName);

	EventStreamResponse saveEventStream(EventStreamResponse eventStream);
}
