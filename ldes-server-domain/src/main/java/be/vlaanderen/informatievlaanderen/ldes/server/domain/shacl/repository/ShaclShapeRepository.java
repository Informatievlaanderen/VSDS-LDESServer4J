package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

import java.util.List;
import java.util.Optional;

public interface ShaclShapeRepository {
	List<ShaclShape> retrieveAllShaclShapes();

	Optional<ShaclShape> retrieveShaclShape(String collectionName);

	ShaclShape saveShaclShape(ShaclShape shaclShape);
}
