package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;

import java.util.List;

public interface EventStreamService {
	List<EventStream> retrieveAllEventStreams();

	EventStream retrieveEventStream(String collectionName);

	void deleteEventStream(String collectionName);

	EventStream saveEventStream(EventStream eventStream);
}
