package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.repository.EventSourceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.service.EventSourceEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EventSourceMongoRepository implements EventSourceRepository {

    private final EventSourceEntityRepository repository;
    private final EventSourceEntityConverter eventSourceEntityConverter;

    public EventSourceMongoRepository(EventSourceEntityRepository repository, EventSourceEntityConverter eventSourceEntityConverter) {
        this.repository = repository;
        this.eventSourceEntityConverter = eventSourceEntityConverter;
    }

    @Override
    public void saveEventSource(EventSource eventSource) {
        repository.save(eventSourceEntityConverter.toEventSourceEntity(eventSource));
    }

    @Override
    public Optional<EventSource> getEventSource(String collectionName) {
        return repository.findById(collectionName).map(eventSourceEntityConverter::toEventSource);
    }

    @Override
    public List<EventSource> getAllEventSources() {
        return repository.findAll().stream().map(eventSourceEntityConverter::toEventSource).toList();
    }
}
