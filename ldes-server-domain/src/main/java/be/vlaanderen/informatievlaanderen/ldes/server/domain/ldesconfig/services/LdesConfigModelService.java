package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;

import java.util.List;

public interface LdesConfigModelService {

	List<LdesConfigModel> retrieveAllConfigModels();

	LdesConfigModel retrieveConfigModel(String collectionName);

	void deleteConfigModel(String collectionName);

	LdesConfigModel updateConfigModel(LdesConfigModel ldesConfigModel);

	List<LdesConfigModel> retrieveAllShapes();

	LdesConfigModel retrieveShape(String collectionName);

	LdesConfigModel updateShape(String collectionName, LdesConfigModel shape);

	List<LdesConfigModel> retrieveViews(String collectionName);

	LdesConfigModel addView(String collectionName, LdesConfigModel view);

	void deleteView(String collectionName, String viewName);

	LdesConfigModel retrieveView(String collectionName, String viewName);
}
