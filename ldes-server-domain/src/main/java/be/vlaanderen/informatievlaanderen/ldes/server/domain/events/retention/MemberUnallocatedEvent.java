package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public record MemberUnallocatedEvent(String memberId, ViewName viewName) {
}
