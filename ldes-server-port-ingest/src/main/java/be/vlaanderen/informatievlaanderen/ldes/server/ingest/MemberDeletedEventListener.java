package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberDeletedEventListener {
	private final MemberRepository memberRepository;

	public MemberDeletedEventListener(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@EventListener
	public void handleMemberDeletedEvent(MemberDeletedEvent event) {
		memberRepository.deleteMember(event.memberId());
	}
}
