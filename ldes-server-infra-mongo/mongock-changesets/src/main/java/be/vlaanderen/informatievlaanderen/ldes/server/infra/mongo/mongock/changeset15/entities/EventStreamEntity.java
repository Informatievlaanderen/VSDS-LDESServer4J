package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.EventStreamUpdaterChange.EVENT_STREAM_COLLECTION_NAME;

@Document(EVENT_STREAM_COLLECTION_NAME)
public class EventStreamEntity {
    @Id
    private final String id;
    private final String versionOfPath;
    private final String timestampPath;

    public EventStreamEntity(String id, String versionOfPath, String timestampPath) {
        this.id = id;
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    public String getId() {
        return id;
    }

    public String getVersionOfPath() {
        return versionOfPath;
    }

    public String getTimestampPath() {
        return timestampPath;
    }
}
