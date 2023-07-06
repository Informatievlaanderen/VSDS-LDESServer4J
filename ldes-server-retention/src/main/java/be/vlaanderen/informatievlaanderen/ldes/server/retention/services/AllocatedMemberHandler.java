package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.context.event.EventListener;

public class AllocatedMemberHandler {
	private final MemberPropertiesRepository memberPropertiesRepository;

	public AllocatedMemberHandler(MemberPropertiesRepository memberPropertiesRepository) {
		this.memberPropertiesRepository = memberPropertiesRepository;
	}

	@EventListener
	public void handleMemberAllocatedEvent(MemberAllocatedEvent event) {
		memberPropertiesRepository.addViewReference(event.memberId(), event.viewName().asString());
	}

}
