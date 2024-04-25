package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;

public class EventStreamConverter {
	public EventStreamEntity fromEventStream(EventStream eventStream) {
		return new EventStreamEntity(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), eventStream.isVersionCreationEnabled());
	}

	public EventStream toEventStream(EventStreamEntity eventStreamEntity) {
		return new EventStream(eventStreamEntity.getId(), eventStreamEntity.getTimestampPath(),
				eventStreamEntity.getVersionOfPath(), eventStreamEntity.isVersionCreationEnabled());
	}
}
