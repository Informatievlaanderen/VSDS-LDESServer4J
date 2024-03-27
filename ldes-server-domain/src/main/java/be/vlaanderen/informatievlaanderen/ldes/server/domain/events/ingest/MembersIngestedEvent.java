package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest;

import java.time.LocalDateTime;
import java.util.List;

public record MembersIngestedEvent(String collectionName, List<MemberProperties> members) {
    public record MemberProperties(String id, String versionOf, LocalDateTime timestamp) {
    }
}
