package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventStreamServiceImpl implements EventStreamService {
	private final EventStreamRepository eventStreamRepository;
	private final ViewService viewService;
	private final ShaclShapeService shaclShapeService;
	private final ApplicationEventPublisher eventPublisher;

	public EventStreamServiceImpl(EventStreamRepository eventStreamRepository, ViewService viewService,
			ShaclShapeService shaclShapeService, ApplicationEventPublisher eventPublisher) {
		this.eventStreamRepository = eventStreamRepository;
		this.viewService = viewService;
		this.shaclShapeService = shaclShapeService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<EventStreamResponse> retrieveAllEventStreams() {
		return eventStreamRepository.retrieveAllEventStreams().stream().map(eventStream -> {
			List<ViewSpecification> views = viewService.getViewsByCollectionName(eventStream.getCollection());
			ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(eventStream.getCollection());
			return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(), eventStream.getMemberType(), eventStream.isDefaultViewEnabled(),
					views, shaclShape.getModel());
		}).toList();
	}

	@Override
	public EventStreamResponse retrieveEventStream(String collectionName) {
		EventStream eventStream = eventStreamRepository.retrieveEventStream(collectionName)
				.orElseThrow(() -> new MissingEventStreamException(collectionName));
		List<ViewSpecification> views = viewService.getViewsByCollectionName(collectionName);
		ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(collectionName);

		return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), eventStream.getMemberType(), eventStream.isDefaultViewEnabled(), views,
				shaclShape.getModel());
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
		if (eventStreamResponse.isDefaultViewEnabled()) {
			viewService.addDefaultView(eventStream.getCollection());
		}
		return eventStreamResponse;
	}
}
