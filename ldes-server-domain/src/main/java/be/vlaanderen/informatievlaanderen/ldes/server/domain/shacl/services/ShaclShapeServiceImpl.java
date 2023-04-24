package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingShaclShapeException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

public class ShaclShapeServiceImpl implements ShaclShapeService {
	private final ShaclCollection shaclCollection;

	public ShaclShapeServiceImpl(ShaclCollection shaclCollection) {
		this.shaclCollection = shaclCollection;
	}

	@Override
	public ShaclShape retrieveShaclShape(String collectionName) {
		return shaclCollection.retrieveShape(collectionName)
				.orElseThrow(() -> new MissingShaclShapeException(collectionName));
	}

	@Override
	public ShaclShape updateShaclShape(ShaclShape shaclShape) {
		shaclCollection.saveShape(shaclShape);
		return shaclShape;
	}
}
