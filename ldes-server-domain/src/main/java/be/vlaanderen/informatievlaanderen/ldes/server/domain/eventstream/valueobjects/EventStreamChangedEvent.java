package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;

public record EventStreamChangedEvent(EventStream eventStream) {

}