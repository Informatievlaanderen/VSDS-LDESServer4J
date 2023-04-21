package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryShaclCollection implements ShaclCollection {
	private final Set<ShaclShape> shapes;
	private final ShaclShapeRepository shaclShapeRepository;

	public InMemoryShaclCollection(ShaclShapeRepository shaclShapeRepository) {
		this.shaclShapeRepository = shaclShapeRepository;
		this.shapes = new HashSet<>();
	}

	@Override
	public void saveShape(ShaclShape shaclShape) {
		shaclShapeRepository.saveShaclShape(shaclShape);
		shapes.remove(shaclShape);
		shapes.add(shaclShape);

	}

	@Override
	public Optional<ShaclShape> retrieveShape(String collectionName) {
		return shapes.stream()
				.filter(shaclShape -> shaclShape.getCollection().equals(collectionName))
				.findFirst();
	}

	@PostConstruct
	private void initShapeConfig() {
		shapes.addAll(shaclShapeRepository.retrieveAllShaclShapes());
	}
}
