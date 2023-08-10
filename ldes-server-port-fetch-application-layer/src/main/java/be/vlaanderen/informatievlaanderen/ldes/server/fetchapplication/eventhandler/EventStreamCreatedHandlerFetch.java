package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.EventStreamProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.HOST_NAME_KEY;

@Component
public class EventStreamCreatedHandlerFetch {

	private final EventStreamRepository eventStreamRepository;
	private final String hostname;

	public EventStreamCreatedHandlerFetch(EventStreamRepository eventStreamRepository,
			@Value(HOST_NAME_KEY) String hostName) {
		this.eventStreamRepository = eventStreamRepository;
		this.hostname = hostName;
	}

	@EventListener
	public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
		eventStreamRepository.saveEventStream(getEventStream(event));
	}

	private EventStream getEventStream(EventStreamCreatedEvent event) {
		return new EventStream(event.eventStream().getCollection(),
				new EventStreamProperties(hostname + "/" + event.eventStream().getCollection(),
						event.eventStream().getTimestampPath(),
						event.eventStream().getVersionOfPath()));
	}
}
