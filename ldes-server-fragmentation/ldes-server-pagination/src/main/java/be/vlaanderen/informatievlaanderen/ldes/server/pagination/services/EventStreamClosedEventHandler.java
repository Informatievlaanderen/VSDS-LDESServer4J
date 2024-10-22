package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class EventStreamClosedEventHandler {
	private final PageRepository pageRepository;

	public EventStreamClosedEventHandler(PageRepository pageRepository) {
		this.pageRepository = pageRepository;
	}

	@EventListener
	public void onEventStreamClosed(EventStreamClosedEvent event) {
		pageRepository.markAllPagesImmutableByCollectionName(event.collectionName());
	}
}
