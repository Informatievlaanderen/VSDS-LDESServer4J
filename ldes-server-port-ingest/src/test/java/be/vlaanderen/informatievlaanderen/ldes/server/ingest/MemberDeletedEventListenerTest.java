package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MembersDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MembersRemovedFromEventSourceEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.listeners.MemberDeletedEventListener;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MemberDeletedEventListenerTest {
	private static final List<String> IDS = List.of("id1", "id2", "id3");
	private final MembersDeletedEvent event = new MembersDeletedEvent(IDS);
	private MemberRepository memberRepository;
	private MemberDeletedEventListener eventHandler;

	@BeforeEach
	void setUp() {
		memberRepository = mock(MemberRepository.class);
		eventHandler = new MemberDeletedEventListener(memberRepository);
	}

	@Test
	void when_MemberDeleted_MethodIsCalled() {
		eventHandler.handleMembersDeletedEvent(event);

		verify(memberRepository).deleteMembers(IDS);
	}

	@Test
	void when_MemberRemovedFromEventSource_MethodIsCalled() {
		eventHandler.handleMembersRemovedFromEventSourceEvent(new MembersRemovedFromEventSourceEvent(IDS));

		verify(memberRepository).removeFromEventSource(IDS);
	}
}
