package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

import java.util.List;

public interface ShaclShapeService {
	List<ShaclShape> retrieveAllShaclShapes();

	ShaclShape retrieveShaclShape(String collectionName);

	ShaclShape updateShaclShape(ShaclShape shaclShape);
}
