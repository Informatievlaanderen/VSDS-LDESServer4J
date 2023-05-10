package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;

import java.util.List;
import java.util.Optional;

public interface EventStreamCollection {
	List<EventStream> retrieveAllEventStreams();

	Optional<EventStream> retrieveEventStream(String collectionName);

	EventStream saveEventStream(EventStream eventStream);

	void deleteEventStream(String collectionName);
}
