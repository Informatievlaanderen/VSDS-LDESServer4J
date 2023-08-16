package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.entities.EventStream;

public record EventStreamCreatedEvent(EventStream eventStream) {

}
