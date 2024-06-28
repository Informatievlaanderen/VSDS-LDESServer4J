package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BucketRelationsEventListener {
	private final PageRelationService pageRelationService;

	public BucketRelationsEventListener(PageRelationService pageRelationService) {
		this.pageRelationService = pageRelationService;
	}

	@EventListener
	public void handleBucketCreatedEvent(BucketCreatedEvent event) {
		final PageRelation pageRelation = PageRelation.fromBucketRelation(event.bucketRelation());
		pageRelationService.insertPageRelation(pageRelation);
	}
}
