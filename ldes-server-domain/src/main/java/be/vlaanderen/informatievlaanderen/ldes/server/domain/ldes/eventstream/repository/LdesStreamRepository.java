package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;

import java.util.List;
import java.util.Optional;

public interface LdesStreamRepository {
	List<LdesStreamModel> retrieveAllLdesStreams();

	Optional<LdesStreamModel> retrieveLdesStream(String collection);

	LdesStreamModel saveLdesStream(LdesStreamModel ldesStreamModel);
}
