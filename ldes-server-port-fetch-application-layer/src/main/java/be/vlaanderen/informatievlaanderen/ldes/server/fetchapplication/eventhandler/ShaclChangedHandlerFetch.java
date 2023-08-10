package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.Shacl;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.ShaclRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShaclChangedHandlerFetch {
	private final ShaclRepository shaclRepository;

	public ShaclChangedHandlerFetch(ShaclRepository shaclRepository) {
		this.shaclRepository = shaclRepository;
	}

	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		shaclRepository.saveShacl(new Shacl(event.getShacl().getCollection(), event.getShacl().getModel()));
	}

}
