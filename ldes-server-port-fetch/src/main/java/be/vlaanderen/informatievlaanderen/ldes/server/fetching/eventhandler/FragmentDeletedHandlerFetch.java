package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.FragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FragmentDeletedHandlerFetch {

	private final AllocationRepository allocationRepository;

	public FragmentDeletedHandlerFetch(AllocationRepository allocationRepository) {
		this.allocationRepository = allocationRepository;
	}

	@EventListener
	public void handleFragmentDeletedEvent(FragmentDeletedEvent event) {
		allocationRepository.deleteByCollectionName(event.ldesFragmentIdentifier().asString());
	}

}
