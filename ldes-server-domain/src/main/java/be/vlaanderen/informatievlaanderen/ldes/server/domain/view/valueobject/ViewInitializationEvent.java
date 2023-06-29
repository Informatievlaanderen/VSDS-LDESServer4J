package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

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
