package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.EventStreamProperties;

public class EventStream {
	private final String collection;
	private final EventStreamProperties eventStreamProperties;

	public EventStream(String collection, EventStreamProperties eventStreamProperties) {
		this.collection = collection;
		this.eventStreamProperties = eventStreamProperties;
	}

	public String getCollection() {
		return collection;
	}

	public EventStreamProperties getEventStreamProperties() {
		return eventStreamProperties;
	}
}
