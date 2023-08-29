package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public record DcatViewDeletedEvent(ViewName viewName) {
}
