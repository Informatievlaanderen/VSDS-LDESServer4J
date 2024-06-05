package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewSupplier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;

/**
 * This event is published as a response to {@link ViewInitializationEvent} or
 * to {@link ViewAddedEvent}. This will trigger rebucketisation of the member for a view.
 */
public record ViewNeedsRebucketisationEvent(ViewSpecification viewSpecification) implements ViewSupplier {
	public ViewName getViewName() {
		return viewSpecification.getName();
	}
}
