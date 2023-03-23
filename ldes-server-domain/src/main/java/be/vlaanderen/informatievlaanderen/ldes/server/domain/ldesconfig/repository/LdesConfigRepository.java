package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;

import java.util.List;
import java.util.Optional;

public interface LdesConfigRepository {
	List<LdesConfigModel> retrieveAllLdesStreams();

	Optional<LdesConfigModel> retrieveLdesStream(String collection);

	LdesConfigModel saveLdesStream(LdesConfigModel ldesConfigModel);

	void deleteLdesStream(String collectionName);
}
