package be.vlaanderen.informatievlaanderen.ldes.server.domain.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryViewCollection implements ViewCollection {
	private final Map<ViewName, ViewSpecification> views;
	private final ViewRepository viewRepository;
	private final ApplicationEventPublisher eventPublisher;

	public InMemoryViewCollection(ViewRepository viewRepository, ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		this.views = new HashMap<>();
		this.viewRepository = viewRepository;
	}

	@Override
	public Optional<ViewSpecification> getViewByViewName(ViewName viewName) {
		return Optional.ofNullable(views.get(viewName));
	}

	@Override
	public void addView(ViewSpecification viewSpecification) {
		viewRepository.saveView(viewSpecification);
		views.put(viewSpecification.getName(), viewSpecification);
		eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
	}

	@Override
	public void deleteViewByViewName(ViewName viewName) {
		viewRepository.deleteViewByViewName(viewName);
		views.remove(viewName);
		eventPublisher.publishEvent(new ViewDeletedEvent(viewName));

	}

	@EventListener(ApplicationStartedEvent.class)
	private void initShapeConfig() {
		viewRepository
				.retrieveAllViews()
				.forEach(viewSpecification -> {
					views.put(viewSpecification.getName(), viewSpecification);
					eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
				});
	}
}
