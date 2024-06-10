package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;

public class EventSourceMapper {
    private EventSourceMapper() {
    }

    public static EventSource fromEntity(EventSourceEntity eventSourceEntity) {
        return new EventSource(eventSourceEntity.getCollectionName(), eventSourceEntity.getRetentionPolicies());
    }
}
