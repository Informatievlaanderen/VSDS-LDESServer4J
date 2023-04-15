package be.vlaanderen.informatievlaanderen.ldes.server.domain.events;

import org.apache.jena.rdf.model.Model;

public class ShaclChangedEvent {
	private final String collectionName;
	private final Model shacl;

	public ShaclChangedEvent(String collectionName, Model shacl) {
		this.collectionName = collectionName;
		this.shacl = shacl;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Model getShacl() {
		return shacl;
	}
}
