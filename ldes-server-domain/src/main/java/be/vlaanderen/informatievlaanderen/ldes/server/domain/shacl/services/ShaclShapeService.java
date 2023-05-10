package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

public interface ShaclShapeService {

	ShaclShape retrieveShaclShape(String collectionName);

	ShaclShape updateShaclShape(ShaclShape shaclShape);

	void deleteShaclShape(String collectionName);
}
