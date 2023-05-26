package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingShaclShapeException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShaclShapeServiceImpl implements ShaclShapeService {
	private final ShaclShapeRepository shaclShapeRepository;
	private final ApplicationEventPublisher eventPublisher;

	public ShaclShapeServiceImpl(ShaclShapeRepository shaclShapeRepository, ApplicationEventPublisher eventPublisher) {
		this.shaclShapeRepository = shaclShapeRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public ShaclShape retrieveShaclShape(String collectionName) {
		return shaclShapeRepository.retrieveShaclShape(collectionName)
				.orElseThrow(() -> new MissingShaclShapeException(collectionName));
	}

	@Override
	public ShaclShape updateShaclShape(ShaclShape shaclShape) {
		shaclShapeRepository.saveShaclShape(shaclShape);
		eventPublisher.publishEvent(new ShaclChangedEvent(shaclShape));
		return shaclShape;
	}

	@Override
	public void deleteShaclShape(String collectionName) {
		shaclShapeRepository.deleteShaclShape(collectionName);
		eventPublisher.publishEvent(new ShaclDeletedEvent(collectionName));
	}

	@EventListener(ApplicationStartedEvent.class)
	public void initShapes() {
		shaclShapeRepository
				.retrieveAllShaclShapes()
				.forEach(shaclShape -> eventPublisher.publishEvent(new ShaclChangedEvent(shaclShape)));
	}
}
