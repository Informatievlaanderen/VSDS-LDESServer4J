package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.EventStreamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStreamCreatedHandlerFetchTest {

	@Mock
	private EventStreamRepository eventStreamRepository;
	@InjectMocks
	private EventStreamCreatedHandlerFetch eventStreamCreatedHandlerFetch;

	@Test
	void when_HandleEventStreamCreatedEvent_EventStreamIsSavedInEventStreamRepository() {
		EventStreamCreatedEvent eventStreamCreatedEvent = new EventStreamCreatedEvent(
				new be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream("", "", "",
						""));

		eventStreamCreatedHandlerFetch.handleEventStreamCreatedEvent(eventStreamCreatedEvent);

		verify(eventStreamRepository).saveEventStream(any(EventStream.class));
	}

}