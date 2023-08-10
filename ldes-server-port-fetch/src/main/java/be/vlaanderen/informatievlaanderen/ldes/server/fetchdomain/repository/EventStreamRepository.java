package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.EventStream;

public interface EventStreamRepository {
	void saveEventStream(EventStream eventStream);

	EventStream getEventStreamByCollection(String collection);

	void deleteEventStreamByCollection(String collection);
}
