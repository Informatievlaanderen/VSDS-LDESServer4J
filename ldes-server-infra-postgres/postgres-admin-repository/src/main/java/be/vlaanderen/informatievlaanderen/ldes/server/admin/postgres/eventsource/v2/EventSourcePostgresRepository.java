package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.mapper.EventSourceMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.repository.EventSourceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventSourcePostgresRepository implements EventSourceRepository {
    private final EventSourceEntityRepository eventSourceEntityRepository;
    private final EventStreamEntityRepository eventStreamEntityRepository;

    public EventSourcePostgresRepository(EventSourceEntityRepository eventSourceEntityRepository, EventStreamEntityRepository eventStreamEntityRepository) {
        this.eventSourceEntityRepository = eventSourceEntityRepository;
        this.eventStreamEntityRepository = eventStreamEntityRepository;
    }

    @Override
    public void saveEventSource(EventSource eventSource) {
        eventStreamEntityRepository.findByName(eventSource.getCollectionName())
                .map(eventStream -> new EventSourceEntity(eventStream, eventSource.getRetentionPolicies()))
                .ifPresent(eventSourceEntityRepository::save);
    }

    @Override
    public Optional<EventSource> getEventSource(String collectionName) {
        return eventSourceEntityRepository.findByCollectionName(collectionName).map(EventSourceMapper::fromEntity);
    }

    @Override
    public List<EventSource> getAllEventSources() {
        return eventSourceEntityRepository.findAll().stream()
                .map(EventSourceMapper::fromEntity)
                .toList();
    }
}
