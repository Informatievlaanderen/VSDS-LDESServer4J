package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventStreamCollectionImpl implements EventStreamCollection {

	private final Map<String, EventStreamProperties> eventStreamMap;

	public EventStreamCollectionImpl() {
		this.eventStreamMap = new HashMap<>();
	}

	public EventStreamProperties getEventStreamProperties(String collectionName) {
		if (eventStreamMap.containsKey(collectionName)) {
			return eventStreamMap.get(collectionName);
		}
		throw new MissingEventStreamException(collectionName);
	}

	@EventListener
	public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
		eventStreamMap.put(event.eventStream().getCollection(),
				new EventStreamProperties(event.eventStream().getVersionOfPath(),
						event.eventStream().getTimestampPath()));
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		// TODO remove all records from table with collection equals
		// event.collectionName()
		eventStreamMap.remove(event.collectionName());
	}
}
