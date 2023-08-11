package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.EventStreamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStreamDeletedHandlerFetchTest {
	private static final String COLLECTION = "collection";
	@Mock
	private EventStreamRepository eventStreamRepository;
	@Mock
	private AllocationRepository allocationRepository;
	@InjectMocks
	private EventStreamDeletedHandlerFetch eventStreamDeletedHandlerFetch;

	@Test
	void when_HandleEventStreamDeletedEvent_EventStreamRepositoryAndAllocationRepositoryAreCalledWithDeleteCommand() {
		EventStreamDeletedEvent eventStreamDeletedEvent = new EventStreamDeletedEvent(COLLECTION);

		eventStreamDeletedHandlerFetch.handleEventStreamDeletedEvent(eventStreamDeletedEvent);

		verify(eventStreamRepository).deleteEventStreamByCollection(COLLECTION);
		verify(allocationRepository).deleteByCollectionName(COLLECTION);
	}
}