package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects;

import org.apache.jena.rdf.model.Model;

public class LdesStreamModel {
	private final String id;
	private final Model model;

	public LdesStreamModel(String id, Model model) {
		this.id = id;
		this.model = model;
	}

	public String getId() {
		return id;
	}

	public Model getModel() {
		return model;
	}
}
