package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection;

import java.time.LocalDateTime;

public interface RetentionMemberProjection {
    long getId();
    String getVersionOf();
    LocalDateTime getTimestamp();
    Boolean getInView();
    Boolean getInEventSource();
    String getCollectionName();
}
