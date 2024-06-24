package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services.EventSourceService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.rdf.model.Model;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventStreamServiceImpl implements EventStreamService {
	private static final String LDES_SERVER_INGESTED_MEMBERS_COUNT = "ldes_server_ingested_members_count";
	public static final String RESOURCE_TYPE = "eventstream";
	private final EventStreamRepository eventStreamRepository;
	private final DcatServerService dcatServerService;
	private final EventSourceService eventSourceService;
	private final ApplicationEventMulticaster eventPublisher;
	private final ViewValidator viewValidator;

	public EventStreamServiceImpl(EventStreamRepository eventStreamRepository,
	                              DcatServerService dcatServerService, EventSourceService eventSourceService,
	                              ApplicationEventMulticaster eventPublisher, ViewValidator viewValidator) {
		this.eventStreamRepository = eventStreamRepository;
		this.dcatServerService = dcatServerService;
		this.eventSourceService = eventSourceService;
		this.eventPublisher = eventPublisher;
		this.viewValidator = viewValidator;
	}

	@Override
	public List<EventStreamTO> retrieveAllEventStreams() {
		return eventStreamRepository.retrieveAllEventStreamTOs();
	}

	@Override
	public EventStreamTO retrieveEventStream(String collectionName) {
		return eventStreamRepository.retrieveEventStreamTO(collectionName)
				.orElseThrow(() -> new MissingResourceException(RESOURCE_TYPE, collectionName));
	}

	@Override
	public void deleteEventStream(String collectionName) {
		final int deletedRows = eventStreamRepository.deleteEventStream(collectionName);
		if (deletedRows == 0) {
			throw new MissingResourceException(RESOURCE_TYPE, collectionName);
		}
		Metrics.globalRegistry.remove(Metrics.counter(LDES_SERVER_INGESTED_MEMBERS_COUNT, "collection", collectionName));
		eventPublisher.multicastEvent(new EventStreamDeletedEvent(this, collectionName));
	}

	@Override
	public EventStreamTO createEventStream(EventStreamTO eventStreamTO) {
		checkCollectionDoesNotYetExist(eventStreamTO.getCollection());
		eventStreamTO.getViews().forEach(viewValidator::validateView);

		eventStreamRepository.saveEventStream(eventStreamTO);
		publishEventStreamTOCreatedEvents(eventStreamTO);

		return eventStreamTO;
	}

	private void checkCollectionDoesNotYetExist(String collectionName) {
		boolean exists = eventStreamRepository.retrieveEventStream(collectionName).isPresent();
		if (exists) {
			throw new IllegalArgumentException("This collection already exists!");
		}
	}

	@Override
	public void updateEventSource(String collectionName, List<Model> eventSourceModel) {
		eventSourceService.saveEventSource(collectionName, eventSourceModel);
	}

	@Override
	public void closeEventStream(String collectionName) {
		EventStream eventStream = getEventStream(collectionName);
		eventPublisher.multicastEvent(new EventStreamClosedEvent(this, eventStream.getCollection()));
	}

	private EventStream getEventStream(String collectionName) {
		return eventStreamRepository.retrieveEventStream(collectionName)
				.orElseThrow(() -> new MissingResourceException(RESOURCE_TYPE, collectionName));
	}

	@Override
	public Model getComposedDcat() {
		return dcatServerService.getComposedDcat();
	}

	/**
	 * Initializes the eventstream config.
	 * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
	 * to give db migrations time before this init.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initEventStream() {
		eventStreamRepository.retrieveAllEventStreams().stream()
				.map(eventStream -> new EventStreamCreatedEvent(this, eventStream))
				.forEach(eventPublisher::multicastEvent);
	}

	private void publishEventStreamTOCreatedEvents(EventStreamTO eventStreamTO) {
		eventPublisher.multicastEvent(new EventStreamCreatedEvent(this, eventStreamTO.extractEventStreamProperties()));
		eventStreamTO.getViews().stream().map(view -> new ViewAddedEvent(this, view)).forEach(eventPublisher::multicastEvent);
		eventPublisher.multicastEvent(new DeletionPolicyChangedEvent(this, eventStreamTO.getCollection(), eventStreamTO.getEventSourceRetentionPolicies()));
	}

}
