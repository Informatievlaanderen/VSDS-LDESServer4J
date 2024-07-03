package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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
				.orElseThrow(() -> new MissingResourceException("shacl-shape", collectionName));
	}

	@Override
	public ShaclShape updateShaclShape(ShaclShape shaclShape) {
		shaclShapeRepository.saveShaclShape(shaclShape);
		eventPublisher.publishEvent(createShaclChangedEvent(shaclShape));
		return shaclShape;
	}

	@Override
	public void deleteShaclShape(String collectionName) {
		shaclShapeRepository.deleteShaclShape(collectionName);
		eventPublisher.publishEvent(new ShaclDeletedEvent(collectionName));
	}

	/**
	 * Initializes the shapes config.
	 * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
	 * to give db migrations time before this init.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initShapes() {
		shaclShapeRepository
				.retrieveAllShaclShapes()
				.forEach(shaclShape -> eventPublisher.publishEvent(createShaclChangedEvent(shaclShape)));
	}

	private ShaclChangedEvent createShaclChangedEvent(ShaclShape shaclShape) {
		return new ShaclChangedEvent(shaclShape.getCollection(), shaclShape.getModel());
	}
}
