package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.eventhandlers;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewCapacityCreator {
	private final ViewCollection viewCollection;

	public ViewCapacityCreator(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		viewCollection
				.saveViewCapacity(new ViewCapacity(event.getViewName(), event.viewSpecification().getPageSize()));
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		viewCollection
				.saveViewCapacity(new ViewCapacity(event.getViewName(), event.viewSpecification().getPageSize()));
	}

}
