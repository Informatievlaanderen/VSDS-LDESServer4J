package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ViewServiceImpl implements ViewService {

	public static final String DEFAULT_VIEW_NAME = "by-page";

	public static final String DEFAULT_VIEW_FRAGMENTATION_STRATEGY = "PaginationFragmentation";

	public static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100",
			"bidirectionalRelations", "false");

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
		if (!eventStreamIsPresent(viewSpecification.getName().getCollectionName())) {
			throw new MissingEventStreamException(viewSpecification.getName().getCollectionName());
		}
		Optional<ViewSpecification> view = viewRepository.getViewByViewName(viewSpecification.getName());
		if (view.isPresent()) {
			throw new DuplicateViewException(viewSpecification.getName());
		}

		eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
		viewRepository.saveView(viewSpecification);
	}

	private boolean eventStreamIsPresent(String collectionName) {
		return eventStreams.containsKey(collectionName);
	}

	@Override
	public void addDefaultView(String collectionName) {
		ViewName defaultViewName = new ViewName(collectionName, DEFAULT_VIEW_NAME);
		if (viewRepository.getViewByViewName(defaultViewName).isEmpty()) {
			FragmentationConfig fragmentation = new FragmentationConfig();
			fragmentation.setName(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
			fragmentation.setConfig(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES);

			ViewSpecification defaultView = new ViewSpecification(defaultViewName, List.of(), List.of(fragmentation));
			addView(defaultView);
		}
	}

	@Override
	public ViewSpecification getViewByViewName(ViewName viewName) {
		if (!eventStreamIsPresent(viewName.getCollectionName())) {
			throw new MissingEventStreamException(viewName.getCollectionName());
		}
		ViewSpecification viewSpecification = viewRepository.getViewByViewName(viewName)
				.orElseThrow(() -> new MissingViewException(viewName));
		addDcatToViewSpecification(viewSpecification);
		return viewSpecification;
	}

	@Override
	public List<ViewSpecification> getViewsByCollectionName(String collectionName) {
		if (!eventStreamIsPresent(collectionName)) {
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
		if (!eventStreamIsPresent(viewName.getCollectionName())) {
			throw new MissingEventStreamException(viewName.getCollectionName());
		}
		Optional<ViewSpecification> view = viewRepository.getViewByViewName(viewName);
		if (view.isEmpty()) {
			throw new MissingViewException(viewName);
		}
		eventPublisher.publishEvent(new ViewDeletedEvent(viewName));
		viewRepository.deleteViewByViewName(viewName);
		dcatViewService.delete(viewName);
	}

	/**
	 * Initializes the views config.
	 * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
	 * to give db migrations such as mongock time before this init.
	 */@EventListener(ApplicationReadyEvent.class)
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
		eventStreams.remove(event.collectionName());
	}

}
