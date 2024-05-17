package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RetentionModelSerializer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.springframework.stereotype.Component;

@Component
public class EventSourceEntityConverter {
	private final RetentionModelSerializer serializer;

	public EventSourceEntityConverter() {
		this.serializer = new RetentionModelSerializer();
	}

	public EventSourceEntity toEventSourceEntity(EventSource eventSource) {
		return new EventSourceEntity(eventSource.getCollectionName(), serializer.serialize(eventSource.getRetentionPolicies()));
	}

	public EventSource toEventSource(EventSourceEntity entity) {
		return new EventSource(entity.getCollectionName(), serializer.deserialize(entity.getRetentionPolicies()));
	}
}
