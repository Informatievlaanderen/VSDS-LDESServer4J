package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.entity.EventStreamEntity;

public class EventStreamConverter {
	public EventStreamEntity fromEventStream(EventStream eventStream) {
		return new EventStreamEntity(eventStream.getCollection(), eventStream.getTimestampPath(), eventStream.getVersionOfPath(), eventStream.getViews());
	}

	public EventStream toEventStream(EventStreamEntity eventStreamEntity) {
		return new EventStream(eventStreamEntity.getId(), eventStreamEntity.getTimestampPath(), eventStreamEntity.getVersionOfPath(), eventStreamEntity.getViews());
	}
}
