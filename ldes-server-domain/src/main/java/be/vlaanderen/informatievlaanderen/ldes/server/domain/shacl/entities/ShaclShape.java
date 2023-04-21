package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class ShaclShape {
	private final String collection;
	private final Model model;

	public ShaclShape(String collection, Model model) {
		this.collection = collection;
		this.model = model;
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
		if (!(o instanceof ShaclShape that))
			return false;
		return Objects.equals(collection, that.collection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection);
	}
}
