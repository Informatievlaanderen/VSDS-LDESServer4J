package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewAddedHandler {
	private final MemberPropertiesRepository memberPropertiesRepository;

	public ViewAddedHandler(MemberPropertiesRepository memberPropertiesRepository) {
		this.memberPropertiesRepository = memberPropertiesRepository;
	}

	@EventListener
	public void handle(ViewAddedEvent event) {
		memberPropertiesRepository.addViewToAll(event.getViewName());
	}

}
