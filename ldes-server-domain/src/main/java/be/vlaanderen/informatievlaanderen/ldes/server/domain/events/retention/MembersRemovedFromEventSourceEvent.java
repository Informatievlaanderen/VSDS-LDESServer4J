package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention;

import java.util.List;

public record MembersRemovedFromEventSourceEvent(List<String> ids) {
}
