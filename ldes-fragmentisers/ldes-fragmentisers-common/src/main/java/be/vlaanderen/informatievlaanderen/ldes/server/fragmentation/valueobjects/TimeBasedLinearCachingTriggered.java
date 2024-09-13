package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import java.time.LocalDateTime;

public record TimeBasedLinearCachingTriggered(long bucketId, LocalDateTime nextUpdateTs) {
}
