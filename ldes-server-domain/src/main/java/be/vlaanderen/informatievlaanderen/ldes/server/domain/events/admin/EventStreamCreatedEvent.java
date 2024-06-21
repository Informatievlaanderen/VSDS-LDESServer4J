package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.springframework.context.ApplicationEvent;

public final class EventStreamCreatedEvent extends ApplicationEvent {
	private final EventStream eventStream;

	public EventStreamCreatedEvent(Object source, EventStream eventStream) {
		super(source);
		this.eventStream = eventStream;
	}

	public EventStream eventStream() {
		return eventStream;
	}

}
