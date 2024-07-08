package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStreamClosedEventHandlerTest {
	@Mock
	private FragmentRepository fragmentRepository;

	@InjectMocks
	private EventStreamClosedEventHandler eventStreamClosedEventHandler;


	@Test
	void when_EventStreamClosedEvent_then_FragmentsAreMadeImmutable() {
		final String collectionName = "collectionName";
		EventStreamClosedEvent event = new EventStreamClosedEvent(collectionName);

		eventStreamClosedEventHandler.onEventStreamClosed(event);

		verify(fragmentRepository).markFragmentsImmutableInCollection(collectionName);
	}

}