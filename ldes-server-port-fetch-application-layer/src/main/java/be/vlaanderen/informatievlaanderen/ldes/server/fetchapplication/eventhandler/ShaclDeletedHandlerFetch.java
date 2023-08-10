package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.ShaclRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShaclDeletedHandlerFetch {
	private final ShaclRepository shaclRepository;

	public ShaclDeletedHandlerFetch(ShaclRepository shaclRepository) {
		this.shaclRepository = shaclRepository;
	}

	@EventListener
	public void handleShaclDeletedEvent(ShaclDeletedEvent event) {
		shaclRepository.deleteShaclByCollection(event.collectionName());
	}

}
