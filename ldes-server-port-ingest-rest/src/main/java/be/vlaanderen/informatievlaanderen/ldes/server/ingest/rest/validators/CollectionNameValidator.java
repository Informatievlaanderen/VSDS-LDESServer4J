package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CollectionNameValidator {

    private final Set<String> collectionNames = new HashSet<>();

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        EventStream eventStream = event.eventStream();
        collectionNames.add(eventStream.getCollection());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        collectionNames.remove(event.collectionName());
    }

    public void validate(String collectionName) {
        if (!collectionNames.contains(collectionName)) {
            throw new MissingResourceException("eventstream", collectionName);
        }
    }
}
