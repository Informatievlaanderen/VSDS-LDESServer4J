package be.vlaanderen.informatievlaanderen.ldes.server.ingest.listeners;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MembersDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberDeletedEventListener {
	private final MemberRepository memberRepository;

	public MemberDeletedEventListener(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@EventListener
	public void handleMembersDeletedEvent(MembersDeletedEvent event) {
		memberRepository.deleteMembers(event.memberIds());
	}
}
