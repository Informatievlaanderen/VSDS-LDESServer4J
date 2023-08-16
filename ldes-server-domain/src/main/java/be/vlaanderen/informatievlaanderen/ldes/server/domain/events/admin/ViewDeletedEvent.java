package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

public class ViewDeletedEvent {
	private final ViewName viewName;

	public ViewDeletedEvent(ViewName viewName) {
		this.viewName = viewName;
	}

	public ViewName getViewName() {
		return viewName;
	}
}
