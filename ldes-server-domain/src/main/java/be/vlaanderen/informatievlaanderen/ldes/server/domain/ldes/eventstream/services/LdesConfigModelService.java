package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface LdesConfigModelService {

	List<LdesConfigModel> retrieveAllEventStreams();

	LdesConfigModel retrieveEventStream(String collectionName);

	LdesConfigModel updateEventStream(LdesConfigModel ldesConfigModel);

	Model retrieveShape(String collectionName);

	LdesConfigModel updateShape(String collectionName, LdesConfigModel shape);

	List<Model> retrieveViews(String collectionName);

	LdesConfigModel addView(String collectionName, LdesConfigModel view);

	Model retrieveView(String collectionName, String viewName);
}
