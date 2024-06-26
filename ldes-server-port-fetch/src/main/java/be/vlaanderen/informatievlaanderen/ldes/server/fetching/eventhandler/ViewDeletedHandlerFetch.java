package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewDeletedHandlerFetch {

	private final AllocationRepository allocationRepository;

	public ViewDeletedHandlerFetch(AllocationRepository allocationRepository) {
		this.allocationRepository = allocationRepository;
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		allocationRepository.deleteByCollectionNameAndViewName(event.getViewName().getCollectionName(),
				event.getViewName().getViewName());
	}
}
