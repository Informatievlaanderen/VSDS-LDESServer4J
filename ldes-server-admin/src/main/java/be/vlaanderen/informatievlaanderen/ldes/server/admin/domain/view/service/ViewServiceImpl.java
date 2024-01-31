package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateRetentionException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ViewServiceImpl implements ViewService {
	private static final String EVENT_STREAM_TYPE = "eventstream";
	private static final String VIEW_TYPE = "view";
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
			throw new MissingResourceException(EVENT_STREAM_TYPE, viewSpecification.getName().getCollectionName());
		}
		Optional<ViewSpecification> view = viewRepository.getViewByViewName(viewSpecification.getName());
		if (view.isPresent()) {
			throw new ExistingResourceException(VIEW_TYPE, viewSpecification.getName().asString());
		}

		checkViewForDuplicateRetentionPolicies(viewSpecification);

		eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
		viewRepository.saveView(viewSpecification);
	}

	private void checkViewForDuplicateRetentionPolicies(ViewSpecification viewSpecification) {
		List<String> duplicateRetentionPolicies = viewSpecification.getRetentionConfigs().stream()
				.map(retentionPolicy -> retentionPolicy.listObjectsOfProperty(RdfConstants.RDF_SYNTAX_TYPE).nextNode())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.map(Object::toString)
				.toList();

		if (!duplicateRetentionPolicies.isEmpty()) {
			throw new DuplicateRetentionException(duplicateRetentionPolicies);
		}
	}

	private boolean isEventStreamMissing(String collectionName) {
		return !eventStreams.containsKey(collectionName);
	}

	@Override
	public ViewSpecification getViewByViewName(ViewName viewName) {
		ViewSpecification viewSpecification = viewRepository.getViewByViewName(viewName)
				.orElseThrow(() -> new MissingResourceException(VIEW_TYPE, viewName.asString()));
		addDcatToViewSpecification(viewSpecification);
		return viewSpecification;
	}

	@Override
	public List<ViewSpecification> getViewsByCollectionName(String collectionName) {
		if (isEventStreamMissing(collectionName)) {
			throw new MissingResourceException(EVENT_STREAM_TYPE, collectionName);
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
			throw new MissingResourceException(EVENT_STREAM_TYPE, viewName.getCollectionName());
		}

		deleteAllViewsByViewName(List.of(viewName));
	}

	@Override
	public void deleteAllViewsByViewName(List<ViewName> viewNames) {
		viewNames.forEach(viewName -> {
			viewRepository.deleteViewByViewName(viewName);
			eventPublisher.publishEvent(new ViewDeletedEvent(viewName));
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
		deleteAllViewsByViewName(viewSpecifications.stream().map(ViewSpecification::getName).toList());
	}

}
