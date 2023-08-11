package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.EventStream;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EventStreamRepositoryImpl implements EventStreamRepository {
	private final HashMap<String, EventStream> eventStreams = new HashMap<>();

	@Override
	public void saveEventStream(EventStream eventStream) {
		eventStreams.put(eventStream.getCollection(), eventStream);
	}

	@Override
	public EventStream getEventStreamByCollection(String collection) {
		return eventStreams.get(collection);
	}

	@Override
	public void deleteEventStreamByCollection(String collection) {
		eventStreams.remove(collection);
	}
}
