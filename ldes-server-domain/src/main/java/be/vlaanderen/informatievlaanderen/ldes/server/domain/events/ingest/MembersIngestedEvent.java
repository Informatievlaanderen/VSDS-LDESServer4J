package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.List;

public class MembersIngestedEvent extends ApplicationEvent {
    private final String collectionName;
    private final List<MemberProperties> members;

    public MembersIngestedEvent(Object source, String collectionName, List<MemberProperties> members) {
        super(source);
        this.collectionName = collectionName;
        this.members = members;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<MemberProperties> getMembers() {
        return members;
    }

    public record MemberProperties(String id, String versionOf, LocalDateTime timestamp) {
    }
}
