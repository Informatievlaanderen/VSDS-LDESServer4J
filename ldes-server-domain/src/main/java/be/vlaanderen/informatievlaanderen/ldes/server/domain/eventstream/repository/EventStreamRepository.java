package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;

import java.util.List;
import java.util.Optional;

public interface EventStreamRepository {
	List<EventStream> retrieveAllEventStreams();

	Optional<EventStream> retrieveEventStream(String collectionName);

	EventStream saveEventStream(EventStream eventStream);
}
