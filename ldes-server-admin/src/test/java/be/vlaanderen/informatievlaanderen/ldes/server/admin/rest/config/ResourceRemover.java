package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResourceRemover {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final List<String> usedStreams = new ArrayList<>();

    protected ResourceRemover(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        usedStreams.add(event.eventStream().getCollection());
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        usedStreams.remove(event.collectionName());
    }

    public void removeUsedResources() {
        List.copyOf(usedStreams).forEach(name -> applicationEventPublisher.publishEvent(new EventStreamDeletedEvent(name)));
    }
}
