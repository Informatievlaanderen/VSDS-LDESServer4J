package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

public class MissingViewException extends RuntimeException {
	private final ViewName viewName;

	public MissingViewException(ViewName viewName) {
		this.viewName = viewName;
	}

	@Override
	public String getMessage() {
		return "Collection " + viewName.getCollectionName() + " does not have a view: " + viewName.getViewName();
	}
}
