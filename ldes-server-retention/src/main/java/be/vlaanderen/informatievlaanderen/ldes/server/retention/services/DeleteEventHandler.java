package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution.MemberRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

//@Component
public class DeleteEventHandler {
	private static final Logger log = LoggerFactory.getLogger(DeleteEventHandler.class);
	private final MemberPropertiesRepository memberPropertiesRepository;
	private final MemberRemover memberRemover;

	public DeleteEventHandler(MemberPropertiesRepository memberPropertiesRepository, MemberRemover memberRemover) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.memberRemover = memberRemover;
	}

//	@Async
//	@EventListener
//	public void handleViewDeletedEvent(ViewDeletedEvent event) {
//		ViewName viewName = event.getViewName();
//		log.atInfo().log("STARTED deleting members of view {} in the background", viewName.asString());
//		memberRemover.removeView(viewName.asString());
//        log.atInfo().log("FINISHED deleting members of view {} in the background", viewName.asString());
//    }

//	@Async
//	@EventListener
//	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
//        log.atInfo().log("STARTED deleting all members of event stream {} in the background", event.collectionName());
//        memberPropertiesRepository.removeMemberPropertiesOfCollection(event.collectionName());
//        log.atInfo().log("FINISHED deleting all members of event stream {} in the background", event.collectionName());
//    }
}
