package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;


import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface LdesStreamModelService {
	String retrieveShape(String collectionName);
	String updateShape(String collectionName, String shape);

	List<Model> retrieveViews(String collectionName);

	LdesStreamModel addView(String collectionName, LdesStreamModel view);

	Model retrieveView(String collectionName, String viewName);
}
