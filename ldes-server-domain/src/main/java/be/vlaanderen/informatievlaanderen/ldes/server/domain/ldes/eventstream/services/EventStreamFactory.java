package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;

public interface EventStreamFactory {
	EventStreamResponse getEventStream();
}
