package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.eventhandlers;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewInitializationHandlerCompaction {
	private final ViewCollection viewCollection;

	public ViewInitializationHandlerCompaction(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		viewCollection
				.saveViewCapacity(new ViewCapacity(event.getViewName(), event.getViewSpecification().getPageSize()));
	}
}
