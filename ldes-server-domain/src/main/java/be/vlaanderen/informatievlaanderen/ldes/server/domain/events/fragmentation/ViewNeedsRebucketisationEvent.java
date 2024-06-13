package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

/**
 * This event is published as a response to {@link ViewInitializationEvent} or
 * to {@link ViewAddedEvent}. This will trigger rebucketisation of the member for a view.
 */
public record ViewNeedsRebucketisationEvent(ViewName viewName) {
}
