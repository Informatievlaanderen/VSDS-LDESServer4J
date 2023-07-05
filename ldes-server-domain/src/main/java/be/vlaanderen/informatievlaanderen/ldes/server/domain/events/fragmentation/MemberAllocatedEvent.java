package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

public record MemberAllocatedEvent(String memberId, ViewName viewName) {
}
