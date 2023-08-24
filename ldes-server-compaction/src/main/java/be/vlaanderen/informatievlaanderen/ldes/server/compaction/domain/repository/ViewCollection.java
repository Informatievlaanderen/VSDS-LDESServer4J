package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.Collection;

public interface ViewCollection {
	void saveViewCapacity(ViewCapacity viewCapacity);

	ViewCapacity getViewCapacityByViewName(ViewName viewName);

	void deleteViewCapacityByViewName(ViewName viewName);

	Collection<ViewCapacity> getAllViewCapacities();
}
