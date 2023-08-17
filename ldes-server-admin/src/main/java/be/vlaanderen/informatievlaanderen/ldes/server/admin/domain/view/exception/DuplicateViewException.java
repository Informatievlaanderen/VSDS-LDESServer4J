package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public class DuplicateViewException extends RuntimeException {

	private final transient ViewName viewName;

	public DuplicateViewException(ViewName viewName) {
		this.viewName = viewName;
	}

	@Override
	public String getMessage() {
		return "Collection " + viewName.getCollectionName() + " already has a view: " + viewName.getViewName();
	}
}
