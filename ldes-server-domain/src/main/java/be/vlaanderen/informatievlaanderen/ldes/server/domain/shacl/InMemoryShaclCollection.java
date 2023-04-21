package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryShaclCollection implements ShaclCollection {
	private final Set<ShaclShape> shapes;
	private final ShaclShapeService shaclShapeService;


	public InMemoryShaclCollection(ShaclShapeService shaclShapeService) {
		this.shaclShapeService = shaclShapeService;
		this.shapes = new HashSet<>();
	}

	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		final ShaclShape shaclShape = event.getShacl();
		shaclShapeService.updateShaclShape(shaclShape);
		shapes.remove(shaclShape);
		shapes.add(shaclShape);

	}

	@Override
	public ShaclShape retrieveShape(String collectionName) {
		return shapes.stream()
				.filter(shaclShape -> shaclShape.getCollection().equals(collectionName))
				.findFirst()
				.orElse(null);
	}

	@PostConstruct
	private void initShapeConfig() {
		shapes.addAll(shaclShapeService.retrieveAllShaclShapes());
	}
}
