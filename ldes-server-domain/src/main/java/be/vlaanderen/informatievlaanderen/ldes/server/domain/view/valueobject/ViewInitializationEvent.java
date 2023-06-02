package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;

public class ViewInitializationEvent {

	private final ViewSpecification viewSpecification;

	public ViewInitializationEvent(ViewSpecification viewSpecification) {
		this.viewSpecification = viewSpecification;
	}

	public ViewName getViewName() {
		return viewSpecification.getName();
	}

	public ViewSpecification getViewSpecification() {
		return viewSpecification;
	}
}
