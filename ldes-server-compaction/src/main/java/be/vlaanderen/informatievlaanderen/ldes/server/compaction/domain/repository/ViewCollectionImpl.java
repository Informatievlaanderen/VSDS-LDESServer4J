package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ViewCollectionImpl implements ViewCollection {
	private final Map<ViewName, ViewCapacity> viewCapacities = new ConcurrentHashMap<>();

	@Override
	public void saveViewCapacity(ViewCapacity viewCapacity) {
		viewCapacities.put(viewCapacity.getViewName(), viewCapacity);
	}

	@Override
	public ViewCapacity getViewCapacityByViewName(ViewName viewName) {
		return viewCapacities.get(viewName);
	}

	@Override
	public void deleteViewCapacityByViewName(ViewName viewName) {
		viewCapacities.remove(viewName);
	}

	@Override
	public Collection<ViewCapacity> getAllViewCapacities() {
		return viewCapacities.values();
	}
}
