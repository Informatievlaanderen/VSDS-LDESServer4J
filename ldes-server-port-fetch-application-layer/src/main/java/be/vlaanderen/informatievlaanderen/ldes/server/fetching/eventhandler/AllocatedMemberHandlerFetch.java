package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.mapper.MemberAllocatedMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AllocatedMemberHandlerFetch {
	private final AllocationRepository allocationRepository;
	private final MemberAllocatedMapper memberAllocatedMapper;

	public AllocatedMemberHandlerFetch(AllocationRepository allocationRepository,
			MemberAllocatedMapper memberAllocatedMapper) {
		this.allocationRepository = allocationRepository;
		this.memberAllocatedMapper = memberAllocatedMapper;
	}

	@EventListener
	public void handleMemberAllocatedEvent(MemberAllocatedEvent event) {
		allocationRepository.saveAllocation(memberAllocatedMapper.toEntity(event));
	}

}
