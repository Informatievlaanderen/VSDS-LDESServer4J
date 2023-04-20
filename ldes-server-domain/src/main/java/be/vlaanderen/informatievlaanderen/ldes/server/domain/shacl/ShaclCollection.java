package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;

public interface ShaclCollection {
	LdesConfigModel retrieveShape(String collectionName);
}
