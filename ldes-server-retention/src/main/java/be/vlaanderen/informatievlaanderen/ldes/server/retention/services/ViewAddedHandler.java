package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ViewAddedHandler {
	private static final Logger log = LoggerFactory.getLogger(ViewAddedHandler.class);
	private final MemberPropertiesRepository memberPropertiesRepository;

	public ViewAddedHandler(MemberPropertiesRepository memberPropertiesRepository) {
		this.memberPropertiesRepository = memberPropertiesRepository;
	}

	@Async
	@EventListener
	public void handle(ViewAddedEvent event) {
		log.atInfo().log("STARTED adding existing members to view {} in the background", event.getViewName().asString());
		memberPropertiesRepository.addViewToAll(event.getViewName());
		log.atInfo().log("FINISHED adding existing members to view {} in the background", event.getViewName().asString());
	}

}
