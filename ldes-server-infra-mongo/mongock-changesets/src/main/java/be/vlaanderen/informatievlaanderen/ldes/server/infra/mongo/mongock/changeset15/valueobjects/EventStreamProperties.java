package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.valueobjects;

public class EventStreamProperties {
    private final String versionOfPath;
    private final String timestampPath;

    public EventStreamProperties(String versionOfPath, String timestampPath) {
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    public String getVersionOfPath() {
        return versionOfPath;
    }

    public String getTimestampPath() {
        return timestampPath;
    }
}
