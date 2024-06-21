package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.apache.jena.rdf.model.Model;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventSourceServiceImpl implements EventSourceService {
    private final EventSourceRepository repository;
    private final ApplicationEventMulticaster eventPublisher;

    public EventSourceServiceImpl(EventSourceRepository repository, ApplicationEventMulticaster eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<EventSource> getEventSource(String collectionName) {
        return repository.getEventSource(collectionName);
    }

    @Override
    public void saveEventSource(String collectionName, List<Model> retentionPolicies) {
        repository.saveEventSource(new EventSource(collectionName, retentionPolicies));
        eventPublisher.multicastEvent(new DeletionPolicyChangedEvent(this, collectionName, retentionPolicies));
    }


    /**
     * Initializes the eventSources.
     * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
     * to give db migrations time before this init.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initViews() {
        repository
                .getAllEventSources()
                .forEach(eventSource -> eventPublisher
                        .multicastEvent(new DeletionPolicyChangedEvent(this, eventSource.getCollectionName(), eventSource.getRetentionPolicies())));
    }
}
