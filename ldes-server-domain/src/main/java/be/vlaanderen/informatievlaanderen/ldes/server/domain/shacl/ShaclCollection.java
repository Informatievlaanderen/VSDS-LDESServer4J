package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

import java.util.Optional;

public interface ShaclCollection {
	Optional<ShaclShape> retrieveShape(String collectionName);

	void saveShape(ShaclShape shaclShape);
}
