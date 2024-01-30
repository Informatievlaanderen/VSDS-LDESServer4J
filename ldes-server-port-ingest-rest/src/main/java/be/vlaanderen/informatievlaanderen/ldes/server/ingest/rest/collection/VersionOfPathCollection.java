package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class VersionOfPathCollection {
	private final Map<String, String> versionOfPaths = new HashMap<>();

	@EventListener
	public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
		EventStream eventStream = event.eventStream();
		versionOfPaths.put(eventStream.getCollection(), eventStream.getVersionOfPath());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		versionOfPaths.remove(event.collectionName());
	}

	public Optional<String> getVersionOfPath(String collectionName) {
		return Optional.ofNullable(versionOfPaths.get(collectionName));
	}
}

