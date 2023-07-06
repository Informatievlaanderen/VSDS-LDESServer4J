package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;

public interface EventStreamCollection {
	EventStreamProperties getEventStreamProperties(String collectionName);
}
