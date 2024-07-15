package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection;

import java.time.LocalDateTime;

public interface RetentionMemberProjection {
    long getId();
    String getVersionOf();
    LocalDateTime getTimestamp();
//    @Value("#{target.bucket.view.name}")
    Boolean getInView();
    Boolean getInEventSource();
//    @Value("#{target.bucket.view.eventStream.name}")
    String getCollectionName();
}
