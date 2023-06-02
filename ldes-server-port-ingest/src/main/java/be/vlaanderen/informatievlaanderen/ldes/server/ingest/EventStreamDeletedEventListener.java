package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventStreamDeletedEventListener {
	private final MemberRepository memberRepository;

	public EventStreamDeletedEventListener(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		memberRepository.deleteMembersByCollection(event.collectionName());
	}
}
