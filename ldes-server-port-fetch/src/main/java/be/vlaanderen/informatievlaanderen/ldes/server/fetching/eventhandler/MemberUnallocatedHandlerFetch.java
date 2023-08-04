package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberUnallocatedHandlerFetch {

	private final AllocationRepository allocationRepository;

	public MemberUnallocatedHandlerFetch(AllocationRepository allocationRepository) {
		this.allocationRepository = allocationRepository;
	}

	@EventListener
	public void handleMemberUnallocatedEvent(MemberUnallocatedEvent event) {
		allocationRepository.deleteByMemberIdAndCollectionNameAndViewName(event.memberId(),
				event.viewName().getCollectionName(), event.viewName().getViewName());
	}
}
