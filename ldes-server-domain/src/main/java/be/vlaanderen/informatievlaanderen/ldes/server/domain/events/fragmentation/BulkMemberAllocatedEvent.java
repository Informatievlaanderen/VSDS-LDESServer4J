package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;


import java.util.List;

public record BulkMemberAllocatedEvent(List<String> membersOfCompactedFragments, String collectionName, String viewName, String fragmentId) {
}
