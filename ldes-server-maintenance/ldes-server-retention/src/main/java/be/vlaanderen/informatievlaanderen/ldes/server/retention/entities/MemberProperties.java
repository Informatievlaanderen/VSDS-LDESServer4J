package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import java.time.LocalDateTime;

public record MemberProperties(Long id, String collectionName, String versionOf, LocalDateTime timestamp,
                               boolean isInEventSource, boolean isInView) {
}
