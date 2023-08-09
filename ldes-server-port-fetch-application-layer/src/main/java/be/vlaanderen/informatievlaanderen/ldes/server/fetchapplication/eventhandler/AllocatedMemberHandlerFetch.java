package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.mapper.MemberAllocatedMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
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
