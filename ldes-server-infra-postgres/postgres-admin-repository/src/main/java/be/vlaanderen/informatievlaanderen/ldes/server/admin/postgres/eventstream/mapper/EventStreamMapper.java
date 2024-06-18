package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;

public class EventStreamMapper {
    private EventStreamMapper() {
    }

    public static EventStream fromEntity(EventStreamEntity entity) {
        return new EventStream(entity.getName(), entity.getTimestampPath(), entity.getVersionOfPath(), entity.getVersionCreationEnabled(), entity.isClosed());
    }

    public static EventStreamEntity toEntity(EventStream eventStream) {
        return new EventStreamEntity(eventStream.getCollection(),
                eventStream.getTimestampPath(),
                eventStream.getVersionOfPath(),
                eventStream.isVersionCreationEnabled(),
                eventStream.isClosed());
    }
}
