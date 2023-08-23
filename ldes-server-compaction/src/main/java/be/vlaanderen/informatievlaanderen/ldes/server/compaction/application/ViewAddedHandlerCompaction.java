package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewAddedHandlerCompaction {
	private final ViewCollection viewCollection;

	public ViewAddedHandlerCompaction(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		viewCollection
				.saveViewCapacity(new ViewCapacity(event.getViewName(), event.getViewSpecification().getPageSize()));
	}

}
