package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.TimeBasedLinearCachingTriggered;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LinearCachingEventsListener {
	private final PageRepository pageRepository;

	public LinearCachingEventsListener(PageRepository pageRepository) {
		this.pageRepository = pageRepository;
	}

	@EventListener
	public void onLinearCachingTriggered(TimeBasedLinearCachingTriggered event) {
		pageRepository.setChildrenImmutableByBucketId(event.bucketId());
	}
}
