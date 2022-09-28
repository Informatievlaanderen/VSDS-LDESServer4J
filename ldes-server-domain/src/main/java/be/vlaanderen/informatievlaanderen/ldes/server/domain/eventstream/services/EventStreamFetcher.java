package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;

public interface EventStreamFetcher {
	EventStream fetchEventStream();
}
