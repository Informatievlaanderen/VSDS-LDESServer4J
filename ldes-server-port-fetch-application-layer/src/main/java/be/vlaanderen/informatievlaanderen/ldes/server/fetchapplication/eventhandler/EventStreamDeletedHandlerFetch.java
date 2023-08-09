package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventStreamDeletedHandlerFetch {

	private final AllocationRepository allocationRepository;

	public EventStreamDeletedHandlerFetch(AllocationRepository allocationRepository) {
		this.allocationRepository = allocationRepository;
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		allocationRepository.deleteByCollectionName(event.collectionName());
	}
}
