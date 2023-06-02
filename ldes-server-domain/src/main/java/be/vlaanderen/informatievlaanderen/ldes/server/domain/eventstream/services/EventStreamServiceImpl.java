package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.springframework.boot.context.event.ApplicationStartedEvent;
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
	public List<EventStreamResponse> retrieveAllEventStreams() {
		return eventStreamRepository.retrieveAllEventStreams().stream().map(eventStream -> {
			List<ViewSpecification> views = viewService.getViewsByCollectionName(eventStream.getCollection());
			ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(eventStream.getCollection());
			Optional<DcatDataset> dataset = dcatDatasetService.retrieveDataset(eventStream.getCollection());
			return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(), eventStream.getMemberType(), eventStream.isDefaultViewEnabled(),
					views, shaclShape.getModel(), dataset.orElse(null));
		}).toList();
	}

	@Override
	public EventStreamResponse retrieveEventStream(String collectionName) {
		EventStream eventStream = eventStreamRepository.retrieveEventStream(collectionName)
				.orElseThrow(() -> new MissingEventStreamException(collectionName));
		List<ViewSpecification> views = viewService.getViewsByCollectionName(collectionName);
		ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(collectionName);
		Optional<DcatDataset> dataset = dcatDatasetService.retrieveDataset(collectionName);

		return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), eventStream.getMemberType(), eventStream.isDefaultViewEnabled(), views,
				shaclShape.getModel(), dataset.orElse(null));
	}

	@Override
	public String retrieveMemberType(String collectionName) {
		return eventStreamRepository.retrieveEventStream(collectionName)
				.map(EventStream::getMemberType)
				.orElseThrow(() -> new MissingEventStreamException(collectionName));
	}

	@Override
	public void deleteEventStream(String collectionName) {
		if (eventStreamRepository.retrieveEventStream(collectionName).isEmpty()) {
			throw new MissingEventStreamException(collectionName);
		}

		eventStreamRepository.deleteEventStream(collectionName);
		viewService.getViewsByCollectionName(collectionName).stream()
				.map(ViewSpecification::getName)
				.forEach(viewService::deleteViewByViewName);
		shaclShapeService.deleteShaclShape(collectionName);
		dcatDatasetService.deleteDataset(collectionName);
		eventPublisher.publishEvent(new EventStreamDeletedEvent(collectionName));
	}

	@Override
	public EventStreamResponse saveEventStream(EventStreamResponse eventStreamResponse) {
		EventStream eventStream = new EventStream(
				eventStreamResponse.getCollection(),
				eventStreamResponse.getTimestampPath(),
				eventStreamResponse.getVersionOfPath(),
				eventStreamResponse.getMemberType(),
				eventStreamResponse.isDefaultViewEnabled());
		ShaclShape shaclShape = new ShaclShape(
				eventStreamResponse.getCollection(),
				eventStreamResponse.getShacl());
		eventStreamRepository.saveEventStream(eventStream);
		shaclShapeService.updateShaclShape(shaclShape);
		eventPublisher.publishEvent(new EventStreamChangedEvent(eventStream));
		eventStreamResponse.getViews().forEach(viewService::addView);
		if (eventStreamResponse.isDefaultViewEnabled()) {
			viewService.addDefaultView(eventStream.getCollection());
		}
		return eventStreamResponse;
	}

	@Override
	public Model getComposedDcat() {
		return dcatServerService.getComposedDcat();
	}

	@EventListener(ApplicationStartedEvent.class)
	public void initEventStream() {
		eventStreamRepository.retrieveAllEventStreams().stream()
				.map(EventStreamChangedEvent::new)
				.forEach(eventPublisher::publishEvent);
	}

}
