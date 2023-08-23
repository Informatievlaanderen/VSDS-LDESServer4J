package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewDeletedHandlerCompaction {
	private final ViewCollection viewCollection;

	public ViewDeletedHandlerCompaction(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		viewCollection.deleteViewCapacityByViewName(event.getViewName());
	}

}
