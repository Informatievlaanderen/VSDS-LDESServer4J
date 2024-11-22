package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services.EventSourceService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.kafkasource.KafkaSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;
import org.apache.jena.rdf.model.Model;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventStreamServiceImpl implements EventStreamService {
	public static final String RESOURCE_TYPE = "eventstream";
	private final EventStreamRepository eventStreamRepository;
	private final KafkaSourceRepository kafkaSourceRepository;
	private final DcatServerService dcatServerService;
	private final EventSourceService eventSourceService;
	private final ApplicationEventPublisher eventPublisher;
	private final ViewValidator viewValidator;

	public EventStreamServiceImpl(EventStreamRepository eventStreamRepository, KafkaSourceRepository kafkaSourceRepository,
	                              DcatServerService dcatServerService, EventSourceService eventSourceService,
	                              ApplicationEventPublisher eventPublisher, ViewValidator viewValidator) {
		this.eventStreamRepository = eventStreamRepository;
		this.kafkaSourceRepository = kafkaSourceRepository;
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
		eventPublisher.publishEvent(new EventStreamDeletedEvent(collectionName));
	}

	@Override
	public EventStreamTO createEventStream(EventStreamTO eventStreamTO) {
		checkCollectionDoesNotYetExist(eventStreamTO.getCollection());
		eventStreamTO.getViews().forEach(viewValidator::validateView);

		var eventStreamId = eventStreamRepository.saveEventStream(eventStreamTO);
		publishEventStreamTOCreatedEvents(eventStreamTO);
		publishKafkaSource(eventStreamTO.getKafkaSourceProperties(), eventStreamId);

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
		eventSourceService.updateEventSource(collectionName, eventSourceModel);
	}

	@Override
	public void closeEventStream(String collectionName) {
		eventStreamRepository.closeEventStream(collectionName);
		EventStream eventStream = getEventStream(collectionName);
		eventPublisher.publishEvent(new EventStreamClosedEvent(eventStream.getCollection()));
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
				.map(EventStreamCreatedEvent::new)
				.forEach(eventPublisher::publishEvent);
		kafkaSourceRepository.getAll().stream()
				.map(KafkaSourceAddedEvent::new)
				.forEach(eventPublisher::publishEvent);
	}

	private void publishEventStreamTOCreatedEvents(EventStreamTO eventStreamTO) {
		eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStreamTO.extractEventStreamProperties()));
		eventStreamTO.getViews().stream().map(ViewAddedEvent::new).forEach(eventPublisher::publishEvent);
		eventPublisher.publishEvent(new DeletionPolicyChangedEvent(eventStreamTO.getCollection(), eventStreamTO.getEventSourceRetentionPolicies()));
	}

	private void publishKafkaSource(Optional<KafkaSourceProperties> kafkaSourceProperties, Integer eventStreamId) {
		if (kafkaSourceProperties.isPresent()) {
			kafkaSourceRepository.save(kafkaSourceProperties.get(), eventStreamId);
			eventPublisher.publishEvent(new KafkaSourceAddedEvent(kafkaSourceProperties.get()));
		}
	}

}
