package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class ShaclChangedEvent {

	private final String collection;
	private final Model model;

	public ShaclChangedEvent(String collectionName, Model shacl) {
		this.collection = collectionName;
		this.model = shacl;
	}

	public String getCollection() {
		return collection;
	}

	public Model getModel() {
		return model;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ShaclChangedEvent that))
			return false;
		return Objects.equals(collection, that.collection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection);
	}
}
