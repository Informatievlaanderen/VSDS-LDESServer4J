package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;

import java.util.List;
import java.util.Optional;

public interface LdesConfigRepository {
	List<LdesConfigModel> retrieveAllConfigModels();

	Optional<LdesConfigModel> retrieveConfigModel(String collection);

	LdesConfigModel saveConfigModel(LdesConfigModel ldesConfigModel);

	void deleteConfigModel(String collectionName);
}
