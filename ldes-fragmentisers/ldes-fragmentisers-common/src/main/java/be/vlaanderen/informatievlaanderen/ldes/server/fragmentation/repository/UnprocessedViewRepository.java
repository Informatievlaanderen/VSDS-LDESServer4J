package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.UnprocessedView;

import java.util.List;

public interface UnprocessedViewRepository {
	List<UnprocessedView> findAll();
}
