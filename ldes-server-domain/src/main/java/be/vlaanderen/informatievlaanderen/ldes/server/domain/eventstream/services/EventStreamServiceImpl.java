package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStream;

import java.util.List;

public class EventStreamServiceImpl implements EventStreamService {
	private final EventStreamCollection eventStreamCollection;

	public EventStreamServiceImpl(EventStreamCollection eventStreamCollection) {
		this.eventStreamCollection = eventStreamCollection;
	}

	@Override
	public List<EventStream> retrieveAllEventStreams() {
		return eventStreamCollection.retrieveAllEventStreams();
	}

	@Override
	public EventStream retrieveEventStream(String collectionName) {
		return eventStreamCollection.retrieveEventStream(collectionName)
				.orElseThrow(() -> new MissingEventStream(collectionName));
	}

	@Override
	public void deleteEventStream(String collectionName) {
		if (eventStreamCollection.retrieveEventStream(collectionName).isEmpty()) {
			throw new MissingEventStream(collectionName);
		}

		eventStreamCollection.deleteEventStream(collectionName);
	}

	@Override
	public EventStream saveEventStream(EventStream eventStream) {
		return eventStreamCollection.saveEventStream(eventStream);
	}
}
