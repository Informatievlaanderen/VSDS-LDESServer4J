package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.springframework.context.ApplicationEvent;

public class ViewDeletedEvent extends ApplicationEvent {
	private final ViewName viewName;

	public ViewDeletedEvent(Object source, ViewName viewName) {
		super(source);
		this.viewName = viewName;
	}

	public ViewName getViewName() {
		return viewName;
	}
}
