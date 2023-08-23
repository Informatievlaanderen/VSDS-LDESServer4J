package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.listeners.EventStreamDeletedEventListener;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStreamDeletedEventListenerTest {
	private static final String COLLECTION = "collection";
	@Mock
	private MemberRepository memberRepository;
	private EventStreamDeletedEventListener eventListener;

	@BeforeEach
	void setUp() {
		eventListener = new EventStreamDeletedEventListener(memberRepository);
	}

	@Test
	void when_EventIsReceived_then_DeleteMembers() {
		eventListener.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION));
		verify(memberRepository).deleteMembersByCollection(COLLECTION);
	}
}