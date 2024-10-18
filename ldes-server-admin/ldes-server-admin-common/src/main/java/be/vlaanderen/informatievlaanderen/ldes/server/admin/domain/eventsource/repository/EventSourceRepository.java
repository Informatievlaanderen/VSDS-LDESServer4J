package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;

import java.util.List;
import java.util.Optional;

public interface EventSourceRepository {
    void saveEventSource(EventSource eventSource);

    Optional<EventSource> getEventSource(String collectionName);

    List<EventSource> getAllEventSources();
}
