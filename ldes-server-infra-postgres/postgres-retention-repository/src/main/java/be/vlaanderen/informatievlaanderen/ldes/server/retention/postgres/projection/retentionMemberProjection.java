package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface retentionMemberProjection {
    long getId();
    String getPartialUrl();
    boolean isImmutable();
    LocalDateTime getNextUpdateTs();
    @Value("#{target.bucket.view.name}")
    String getViewName();
    @Value("#{target.bucket.view.eventStream.name}")
    String getCollectionName();
}
