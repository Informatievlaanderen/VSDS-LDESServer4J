package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventStreamServiceImpl implements EventStreamService {
	private final EventStreamRepository eventStreamRepository;
	private final ViewService viewService;
	private final ShaclShapeService shaclShapeService;
	private final DcatServerService dcatServerService;
	private final DcatDatasetService dcatDatasetService;
	private final ApplicationEventPublisher eventPublisher;

	public EventStreamServiceImpl(EventStreamRepository eventStreamRepository, ViewService viewService,
			ShaclShapeService shaclShapeService, DcatDatasetService dcatDatasetService,
			DcatServerService dcatServerService, ApplicationEventPublisher eventPublisher) {
		this.eventStreamRepository = eventStreamRepository;
		this.viewService = viewService;
		this.shaclShapeService = shaclShapeService;
		this.dcatServerService = dcatServerService;
		this.dcatDatasetService = dcatDatasetService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<EventStreamTO> retrieveAllEventStreams() {
		return eventStreamRepository.retrieveAllEventStreams().stream().map(eventStream -> {
			List<ViewSpecification> views = viewService.getViewsByCollectionName(eventStream.getCollection());
			ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(eventStream.getCollection());
			Optional<DcatDataset> dataset = dcatDatasetService.retrieveDataset(eventStream.getCollection());
			return new EventStreamTO(eventStream.getCollection(), eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(), eventStream.isVersionCreationEnabled(),
					views, shaclShape.getModel(), dataset.orElse(null));
		}).toList();
	}

	@Override
	public EventStreamTO retrieveEventStream(String collectionName) {
		EventStream eventStream = eventStreamRepository.retrieveEventStream(collectionName)
				.orElseThrow(() -> new MissingResourceException("eventstream", collectionName));
		List<ViewSpecification> views = viewService.getViewsByCollectionName(collectionName);
		ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(collectionName);
		Optional<DcatDataset> dataset = dcatDatasetService.retrieveDataset(collectionName);

		return new EventStreamTO(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), eventStream.isVersionCreationEnabled(), views,
				shaclShape.getModel(), dataset.orElse(null));
	}

	@Override
	public void deleteEventStream(String collectionName) {
		if (eventStreamRepository.retrieveEventStream(collectionName).isEmpty()) {
			throw new MissingResourceException("eventstream", collectionName);
		}

		delete(collectionName);
	}

	@Override
	public EventStreamTO createEventStream(EventStreamTO eventStreamTO) {
		EventStream eventStream = mapToEventStream(eventStreamTO);
		ShaclShape shaclShape = new ShaclShape(eventStreamTO.getCollection(), eventStreamTO.getShacl());

		checkCollectionDoesNotYetExist(eventStream.getCollection());

		try {
			eventStreamRepository.saveEventStream(eventStream);
			shaclShapeService.updateShaclShape(shaclShape);
			eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));
			eventStreamTO.getViews().forEach(viewService::addView);
		} catch (RuntimeException e) {
			delete(eventStreamTO.getCollection());
			throw e;
		}

		return eventStreamTO;
	}

	private void delete(String collectionName) {
		eventStreamRepository.deleteEventStream(collectionName);
		eventPublisher.publishEvent(new EventStreamDeletedEvent(collectionName));
	}

	private void checkCollectionDoesNotYetExist(String collectionName) {
		boolean exists = eventStreamRepository.retrieveEventStream(collectionName).isPresent();
		if (exists) {
			throw new IllegalArgumentException("This collection already exists!");
		}
	}

	private EventStream mapToEventStream(EventStreamTO eventStreamTO) {
		return new EventStream(
				eventStreamTO.getCollection(),
				eventStreamTO.getTimestampPath(),
				eventStreamTO.getVersionOfPath(),
				eventStreamTO.isVersionCreationEnabled()
		);
	}

	@Override
	public Model getComposedDcat() {
		return dcatServerService.getComposedDcat();
	}

	/**
	 * Initializes the eventstream config.
	 * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
	 * to give db migrations such as mongock time before this init.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initEventStream() {
		eventStreamRepository.retrieveAllEventStreams().stream()
				.map(EventStreamCreatedEvent::new)
				.forEach(eventPublisher::publishEvent);
	}

}
