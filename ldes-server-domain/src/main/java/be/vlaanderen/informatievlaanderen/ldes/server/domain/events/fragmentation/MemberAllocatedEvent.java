package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;


public record MemberAllocatedEvent(String memberId, String collectionName, String viewName, String fragmentId) {
}
