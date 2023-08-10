package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities;

import org.apache.jena.rdf.model.Model;

public class Shacl {
	private final String collection;
	private final Model model;

	public Shacl(String collection, Model model) {
		this.collection = collection;
		this.model = model;
	}

	public String getCollection() {
		return collection;
	}

	public Model getModel() {
		return model;
	}
}
