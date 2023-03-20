package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface LdesStreamModelService {

	List<LdesStreamModel> retrieveAllEventStreams();

	LdesStreamModel retrieveEventStream(String collectionName);

	LdesStreamModel updateEventStream(LdesStreamModel ldesStreamModel);

	Model retrieveShape(String collectionName);

	LdesStreamModel updateShape(String collectionName, LdesStreamModel shape);

	List<Model> retrieveViews(String collectionName);

	LdesStreamModel addView(String collectionName, LdesStreamModel view);

	Model retrieveView(String collectionName, String viewName);
}
