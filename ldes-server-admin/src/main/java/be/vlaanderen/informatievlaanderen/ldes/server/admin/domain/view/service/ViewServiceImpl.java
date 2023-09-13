package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ViewServiceImpl implements ViewService {

	private final DcatViewService dcatViewService;
	private final ViewRepository viewRepository;
	private final ApplicationEventPublisher eventPublisher;

	private final HashMap<String, EventStream> eventStreams = new HashMap<>();

	public ViewServiceImpl(DcatViewService dcatViewService, ViewRepository viewRepository,
			ApplicationEventPublisher eventPublisher) {
		this.dcatViewService = dcatViewService;
		this.viewRepository = viewRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void addView(ViewSpecification viewSpecification) {
		if (isEventStreamMissing(viewSpecification.getName().getCollectionName())) {
			throw new MissingEventStreamException(viewSpecification.getName().getCollectionName());
		}
		Optional<ViewSpecification> view = viewRepository.getViewByViewName(viewSpecification.getName());
		if (view.isPresent()) {
			throw new DuplicateViewException(viewSpecification.getName());
		}

		eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
		viewRepository.saveView(viewSpecification);
	}

	private boolean isEventStreamMissing(String collectionName) {
		return !eventStreams.containsKey(collectionName);
	}

	@Override
	public ViewSpecification getViewByViewName(ViewName viewName) {
		ViewSpecification viewSpecification = viewRepository.getViewByViewName(viewName)
				.orElseThrow(() -> new MissingViewException(viewName));
		addDcatToViewSpecification(viewSpecification);
		return viewSpecification;
	}

	@Override
	public List<ViewSpecification> getViewsByCollectionName(String collectionName) {
		if (isEventStreamMissing(collectionName)) {
			throw new MissingEventStreamException(collectionName);
		}
		List<ViewSpecification> viewSpecifications = viewRepository.retrieveAllViewsOfCollection(collectionName);
		viewSpecifications.forEach(this::addDcatToViewSpecification);
		return viewSpecifications;
	}

	private void addDcatToViewSpecification(ViewSpecification viewSpecification) {
		dcatViewService.findByViewName(viewSpecification.getName()).ifPresent(viewSpecification::setDcat);
	}

	@Override
	public void deleteViewByViewName(ViewName viewName) {
		if (isEventStreamMissing(viewName.getCollectionName())) {
			throw new MissingEventStreamException(viewName.getCollectionName());
		}

		deleteViews(List.of(viewName));
	}

	private void deleteViews(List<ViewName> viewNames) {
		viewNames.forEach(viewName -> {
			eventPublisher.publishEvent(new ViewDeletedEvent(viewName));
			viewRepository.deleteViewByViewName(viewName);
		});
	}

	/**
	 * Initializes the views config.
	 * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
	 * to give db migrations such as mongock time before this init.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initViews() {
		viewRepository
				.retrieveAllViews()
				.forEach(viewSpecification -> eventPublisher
						.publishEvent(new ViewInitializationEvent(viewSpecification)));
	}

	@EventListener
	public void handleEventStreamInitEvent(EventStreamCreatedEvent event) {
		eventStreams.put(event.eventStream().getCollection(), event.eventStream());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		String collectionName = event.collectionName();
		eventStreams.remove(collectionName);
		List<ViewSpecification> viewSpecifications = viewRepository.retrieveAllViewsOfCollection(collectionName);
		deleteViews(viewSpecifications.stream().map(ViewSpecification::getName).toList());
	}

}
