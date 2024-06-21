package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import org.springframework.context.ApplicationEvent;

public final class EventStreamDeletedEvent extends ApplicationEvent {
	private final String collectionName;

	public EventStreamDeletedEvent(Object source, String collectionName) {
		super(source);
		this.collectionName = collectionName;
	}

	public String collectionName() {
		return collectionName;
	}
}
