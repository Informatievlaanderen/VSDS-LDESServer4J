package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;

import java.time.LocalDateTime;

public record TimeBasedLinearCachingTriggered(long bucketId, LocalDateTime nextUpdateTs) {
}
