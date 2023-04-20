package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

public interface ShaclCollection {
	ShaclShape retrieveShape(String collectionName);
}
