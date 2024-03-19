package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest;

import java.time.LocalDateTime;

public record MemberIngestedEvent(String id, String collectionName, long sequenceNr, String versionOf, LocalDateTime timestamp) {
}
