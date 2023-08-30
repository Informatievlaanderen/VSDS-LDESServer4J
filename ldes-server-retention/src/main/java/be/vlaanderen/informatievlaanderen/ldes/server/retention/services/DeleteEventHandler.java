package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution.MemberRemover;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DeleteEventHandler {
	private final MemberPropertiesRepository memberPropertiesRepository;
	private final MemberRemover memberRemover;

	public DeleteEventHandler(MemberPropertiesRepository memberPropertiesRepository, MemberRemover memberRemover) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.memberRemover = memberRemover;
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		String viewName = event.getViewName().asString();
		memberPropertiesRepository.getMemberPropertiesWithViewReference(viewName)
				.forEach(memberProperties -> memberRemover.removeMemberFromView(memberProperties, viewName));
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		memberPropertiesRepository.removeMemberPropertiesOfCollection(event.collectionName());
	}
}
