package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

public class DuplicateViewException extends RuntimeException {
	private final ViewName viewName;

	public DuplicateViewException(ViewName viewName) {
		this.viewName = viewName;
	}

	@Override
	public String getMessage() {
		return "Collection " + viewName.getCollectionName() + " already has a view: " + viewName.asString();
	}
}
