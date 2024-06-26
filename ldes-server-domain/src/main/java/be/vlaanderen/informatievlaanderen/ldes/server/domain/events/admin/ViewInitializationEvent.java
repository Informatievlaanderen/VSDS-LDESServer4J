package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;

/**
 * This event is published on application startup to load the existing views.
 * For new views being added to the server, refer to {@link ViewAddedEvent}
 */
public record ViewInitializationEvent(ViewSpecification viewSpecification) implements ViewSupplier {
	public ViewName getViewName() {
		return viewSpecification.getName();
	}

}
