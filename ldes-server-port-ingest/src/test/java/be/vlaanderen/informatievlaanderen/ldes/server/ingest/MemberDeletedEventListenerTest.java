package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MemberDeletedEventListenerTest {
	private static final String ID = "id";
	private final MemberDeletedEvent event = new MemberDeletedEvent(ID);
	private MemberRepository memberRepository;
	private MemberDeletedEventListener eventHandler;

	@BeforeEach
	void setUp() {
		memberRepository = mock(MemberRepository.class);
		eventHandler = new MemberDeletedEventListener(memberRepository);
	}

	@Test
	void when_MemberDeleted_MethodIsCalled() {
		eventHandler.handleMemberDeletedEvent(event);

		verify(memberRepository).deleteMember(ID);
	}
}
