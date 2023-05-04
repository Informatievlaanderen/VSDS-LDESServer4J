package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class InMemoryShaclCollection implements ShaclCollection {

	private final Set<ShaclShape> shapes;
	private final ShaclShapeRepository shaclShapeRepository;
	private final ApplicationEventPublisher eventPublisher;

	public InMemoryShaclCollection(ShaclShapeRepository shaclShapeRepository,
			ApplicationEventPublisher eventPublisher) {
		this.shaclShapeRepository = shaclShapeRepository;
		this.eventPublisher = eventPublisher;
		this.shapes = new HashSet<>();
	}

	@Override
	public void saveShape(ShaclShape shaclShape) {
		shaclShapeRepository.saveShaclShape(shaclShape);
		shapes.remove(shaclShape);
		shapes.add(shaclShape);

	}

	@Override
	public void deleteShape(String collectionName) {
		shaclShapeRepository.deleteShaclShape(collectionName);
		shapes.removeIf(shaclShape -> shaclShape.getCollection().equals(collectionName));
	}

	@Override
	public Optional<ShaclShape> retrieveShape(String collectionName) {
		return shapes.stream()
				.filter(shaclShape -> shaclShape.getCollection().equals(collectionName))
				.findFirst();
	}

	@EventListener(ApplicationStartedEvent.class)
	public void initShapeConfig() {
		shaclShapeRepository
				.retrieveAllShaclShapes()
				.forEach(shaclShape -> {
					shapes.add(shaclShape);
					eventPublisher.publishEvent(new ShaclChangedEvent(shaclShape));
				});
	}

}
