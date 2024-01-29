package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VersionOfPathCollection {
	private final Map<String, String> collectionNames = new HashMap<>();

	@EventListener
	public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
		EventStream eventStream = event.eventStream();
		collectionNames.put(eventStream.getCollection(), eventStream.getVersionOfPath());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		collectionNames.remove(event.collectionName());
	}

	public String getVersionOfPath(String collectionName) {
		String versionOfPath = collectionNames.get(collectionName);
		if(versionOfPath == null) {
			throw new MissingResourceException("eventstream", collectionName);
		}
		return versionOfPath;
	}
}

