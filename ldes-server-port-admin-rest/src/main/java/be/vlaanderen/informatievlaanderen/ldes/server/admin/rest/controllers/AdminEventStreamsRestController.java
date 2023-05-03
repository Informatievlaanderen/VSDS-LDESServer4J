package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/v1/eventstreams")
public class AdminEventStreamsRestController {

	private final EventStreamService eventStreamService;
	private final ShaclShapeService shaclShapeService;
	private final ViewService viewService;
	private final EventStreamResponseConverter eventStreamResponseConverter = new EventStreamResponseConverter();
	private final EventStreamValidator eventStreamValidator;

	public AdminEventStreamsRestController(EventStreamService eventStreamService, ShaclShapeService shaclShapeService,
			ViewService viewService, EventStreamValidator eventStreamValidator) {
		this.eventStreamService = eventStreamService;
		this.shaclShapeService = shaclShapeService;
		this.viewService = viewService;
		this.eventStreamValidator = eventStreamValidator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(eventStreamValidator);
	}

	@GetMapping
	public List<EventStreamResponse> getEventStreams() {
		return eventStreamService.retrieveAllEventStreams().stream().map(eventStream -> {
			List<ViewSpecification> views = viewService.getViewsByCollectionName(eventStream.getCollection());
			ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(eventStream.getCollection());
			return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(), views, shaclShape.getModel());
		}).toList();

	}

	@PutMapping
	public EventStreamResponse putEventStream(@RequestBody @Validated Model eventStreamModel) {
		EventStreamResponse eventStreamResponse = eventStreamResponseConverter.fromModel(eventStreamModel);
		EventStream eventStream = new EventStream(
				eventStreamResponse.getCollection(),
				eventStreamResponse.getTimestampPath(),
				eventStreamResponse.getVersionOfPath());
		ShaclShape shaclShape = new ShaclShape(
				eventStreamResponse.getCollection(),
				eventStreamResponse.getShacl());
		eventStreamResponse.getViews().forEach(viewService::addView);
		eventStreamService.saveEventStream(eventStream);
		shaclShapeService.updateShaclShape(shaclShape);
		return eventStreamResponse;
	}

	@GetMapping("/{collectionName}")
	public EventStreamResponse getEventStream(@PathVariable String collectionName) {
		EventStream eventStream = eventStreamService.retrieveEventStream(collectionName);
		List<ViewSpecification> views = viewService.getViewsByCollectionName(collectionName);
		ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(collectionName);

		return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), views, shaclShape.getModel());
	}

	@DeleteMapping("/{collectionName}")
	public ResponseEntity<Object> deleteEventStream(@PathVariable String collectionName) {
		// TODO: delete views by collectionName when this is added to the service
		eventStreamService.deleteEventStream(collectionName);
		viewService.getViewsByCollectionName(collectionName).stream()
				.map(ViewSpecification::getName)
				.forEach(viewService::deleteViewByViewName);
		shaclShapeService.deleteShaclShape(collectionName);
		return ResponseEntity.ok().build();
	}

}
