package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.context.event.EventListener;

public class AllocatedMemberHandler {

	private final MemberPropertiesRepository repository;

	public AllocatedMemberHandler(MemberPropertiesRepository repository) {
		this.repository = repository;
	}

	@EventListener
	public void handleEventMemberIngestedEvent(MemberAllocatedEvent event) {
		repository.allocateMember(event.memberId(), event.viewName());
	}

}
