package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelationCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BucketRelationsEventListener {
	private final PageRelationRepository pageRelationRepository;

	public BucketRelationsEventListener(PageRelationRepository pageRelationRepository) {
		this.pageRelationRepository = pageRelationRepository;
	}

	@EventListener
	public void onBucketRelationCreatedEvent(BucketRelationCreatedEvent event) {
		PageRelation pageRelation = PageRelation.fromBucketRelation(event.bucketRelation());
		pageRelationRepository.insertPageRelation(pageRelation);
	}
}
