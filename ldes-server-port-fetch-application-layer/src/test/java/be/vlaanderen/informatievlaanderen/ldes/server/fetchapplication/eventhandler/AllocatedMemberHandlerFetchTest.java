package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.mapper.MemberAllocatedMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AllocatedMemberHandlerFetchTest {

	@Mock
	private AllocationRepository allocationRepository;
	@Spy
	private MemberAllocatedMapper memberAllocatedMapper;
	@InjectMocks
	private AllocatedMemberHandlerFetch allocatedMemberHandlerFetch;

	@Test
	void when_HandleMemberAllocatedEvent_MemberAllocationIsSavedInAllocationRepository() {
		MemberAllocatedEvent memberAllocatedEvent = new MemberAllocatedEvent("id", "collectionName", "viewName",
				"fragmentId");

		allocatedMemberHandlerFetch.handleMemberAllocatedEvent(memberAllocatedEvent);

		verify(allocationRepository).saveAllocation(any(MemberAllocation.class));
	}
}