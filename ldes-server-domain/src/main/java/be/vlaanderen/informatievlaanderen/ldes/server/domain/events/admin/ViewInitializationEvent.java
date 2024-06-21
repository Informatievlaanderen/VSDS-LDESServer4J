package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.springframework.context.ApplicationEvent;

/**
 * This event is published on application startup to load the existing views.
 * For new views being added to the server, refer to {@link ViewAddedEvent}
 */
public final class ViewInitializationEvent extends ApplicationEvent implements ViewSupplier {
	private final ViewSpecification viewSpecification;

	public ViewInitializationEvent(Object source, ViewSpecification viewSpecification) {
		super(source);
		this.viewSpecification = viewSpecification;
	}

	public ViewName getViewName() {
		return viewSpecification.getName();
	}

	@Override
	public ViewSpecification viewSpecification() {
		return viewSpecification;
	}

}
