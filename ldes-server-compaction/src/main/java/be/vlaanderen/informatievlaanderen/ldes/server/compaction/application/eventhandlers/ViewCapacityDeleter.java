package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.eventhandlers;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewCapacityDeleter {
	private final ViewCollection viewCollection;

	public ViewCapacityDeleter(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		viewCollection.deleteViewCapacityByViewName(event.getViewName());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		viewCollection.deleteViewCapacitiesByViewName(event.collectionName());
	}

}
