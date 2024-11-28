package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.apache.jena.rdf.model.Model;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventSourceServiceImpl implements EventSourceService {
    private final EventSourceRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public EventSourceServiceImpl(EventSourceRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<EventSource> getEventSource(String collectionName) {
        return repository.getEventSource(collectionName);
    }

    @Override
    public void updateEventSource(String collectionName, List<Model> retentionPolicies) {
        if(repository.getEventSource(collectionName).isEmpty()) {
            throw new MissingResourceException("eventstream", collectionName);
        }
        repository.saveEventSource(new EventSource(collectionName, retentionPolicies));
        eventPublisher.publishEvent(new DeletionPolicyChangedEvent(collectionName, retentionPolicies));
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
                        .publishEvent(new DeletionPolicyChangedEvent(eventSource.collectionName(), eventSource.retentionPolicies())));
    }
}
