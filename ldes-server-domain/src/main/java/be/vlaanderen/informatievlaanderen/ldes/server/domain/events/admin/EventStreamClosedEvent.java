package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import org.springframework.context.ApplicationEvent;

public final class EventStreamClosedEvent extends ApplicationEvent {
	private final String collectionName;

	public EventStreamClosedEvent(Object source, String collectionName) {
		super(source);
		this.collectionName = collectionName;
	}

	public String collectionName() {
		return collectionName;
	}
}
