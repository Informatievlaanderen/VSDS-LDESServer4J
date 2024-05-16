package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RetentionModelSerializer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.springframework.stereotype.Component;

@Component
public class EventSourceEntityConverter {
    private final RetentionModelSerializer serializer;

    public EventSourceEntityConverter(RetentionModelSerializer serializer) {
        this.serializer = serializer;
    }

    public EventSourceEntity toEventSourceEntity(EventSource eventSource) {
        return new EventSourceEntity(eventSource.getCollectionName(), serializer.serialize(eventSource.getRetentionPolicies()));
    }

    public EventSource toEventSource(EventSourceEntity entity) {
        return new EventSource(entity.getCollectionName(), serializer.deserialize(entity.getRetentionPolicies()));
    }
}
