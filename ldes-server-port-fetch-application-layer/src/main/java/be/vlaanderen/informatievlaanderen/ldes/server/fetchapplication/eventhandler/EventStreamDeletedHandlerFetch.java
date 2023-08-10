package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.EventStreamRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventStreamDeletedHandlerFetch {

	private final AllocationRepository allocationRepository;
	private final EventStreamRepository eventStreamRepository;

	public EventStreamDeletedHandlerFetch(AllocationRepository allocationRepository,
			EventStreamRepository eventStreamRepository) {
		this.allocationRepository = allocationRepository;
		this.eventStreamRepository = eventStreamRepository;
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		allocationRepository.deleteByCollectionName(event.collectionName());
		eventStreamRepository.deleteEventStreamByCollection(event.collectionName());
	}
}
