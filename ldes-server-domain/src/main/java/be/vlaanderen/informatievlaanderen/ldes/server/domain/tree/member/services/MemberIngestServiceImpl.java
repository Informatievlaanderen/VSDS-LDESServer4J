package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberIngestServiceImpl {
	private final MemberRepository memberRepository;

	public MemberIngestServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		memberRepository.deleteMembersByCollection(event.collectionName());
	}

}
