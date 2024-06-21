package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.springframework.context.ApplicationEvent;

/**
 * This event is published when a new view is created.
 * To communicate view config on startup, refer to {@link ViewInitializationEvent}.
 */
public class ViewAddedEvent extends ApplicationEvent implements ViewSupplier {
	private final ViewSpecification viewSpecification;

	public ViewAddedEvent(Object source, ViewSpecification viewSpecification) {
		super(source);
		this.viewSpecification = viewSpecification;
	}

	public ViewName getViewName() {
		return viewSpecification.getName();
	}

	public ViewSpecification viewSpecification() {
		return viewSpecification;
	}
}
