package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingShaclShapeException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class ShaclShapeServiceImpl implements ShaclShapeService {
	private final ShaclShapeRepository repository;
	private final ApplicationEventPublisher eventPublisher;

	public ShaclShapeServiceImpl(ShaclShapeRepository repository, ApplicationEventPublisher eventPublisher) {
		this.repository = repository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<ShaclShape> retrieveAllShaclShapes() {
		return repository.retrieveAllShaclShapes();
	}

	@Override
	public ShaclShape retrieveShaclShape(String collectionName) {
		return repository.retrieveShaclShape(collectionName)
				.orElseThrow(() -> new MissingShaclShapeException(collectionName));
	}

	@Override
	public ShaclShape updateShaclShape(ShaclShape shaclShape) {
		if (!repository.existByCollection(shaclShape.getCollection())) {
			throw new MissingShaclShapeException(shaclShape.getCollection());
		}

		repository.saveShaclShape(shaclShape);
		eventPublisher.publishEvent(new ShaclChangedEvent(shaclShape));
		return shaclShape;
	}
}
