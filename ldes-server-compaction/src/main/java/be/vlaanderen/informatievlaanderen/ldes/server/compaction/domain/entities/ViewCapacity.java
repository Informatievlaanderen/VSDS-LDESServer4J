package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public class ViewCapacity {
	private final ViewName viewName;
	private final int capacityPerPage;

	public ViewCapacity(ViewName viewName, int capacityPerPage) {
		this.viewName = viewName;
		this.capacityPerPage = capacityPerPage;
	}

	public ViewName getViewName() {
		return viewName;
	}

	public int getCapacityPerPage() {
		return capacityPerPage;
	}
}
