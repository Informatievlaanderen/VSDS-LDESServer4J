package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ViewServiceImpl implements ViewService {

	private final ViewRepository viewRepository;
	private final ApplicationEventPublisher eventPublisher;

	public ViewServiceImpl(ViewRepository viewRepository, ApplicationEventPublisher eventPublisher) {
		this.viewRepository = viewRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void addView(ViewSpecification viewSpecification) {
		Optional<ViewSpecification> view = viewRepository.getViewByViewName(viewSpecification.getName());
		if (view.isEmpty()) {
			viewRepository.saveView(viewSpecification);
			eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
		} else {
			throw new DuplicateViewException(viewSpecification.getName());
		}
	}

	@Override
	public ViewSpecification getViewByViewName(ViewName viewName) {
		throw new NotImplementedException();
	}

	@Override
	public List<ViewSpecification> getViewsByCollectionName(String collectionName) {
		throw new NotImplementedException();
	}

	@Override
	public void deleteViewByViewName(ViewName viewName) {
		viewRepository.deleteViewByViewName(viewName);
		eventPublisher.publishEvent(new ViewDeletedEvent(viewName));
	}

	@EventListener(ApplicationStartedEvent.class)
	public void initViews() {
		viewRepository
				.retrieveAllViews()
				.forEach(viewSpecification -> eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification)));
	}
}
