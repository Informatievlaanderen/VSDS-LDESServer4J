package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.OpenAPIConfig.*;

@RestController
@RequestMapping(value = "/admin/api/v1/eventstreams")
@Tag(name = "Event Stream")
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

	@GetMapping
	@Operation(summary = "Retrieves list of configured Event Streams")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = NQUADS),
			@Content(mediaType = JSON_LD),
			@Content(mediaType = TURTLE)
	})
	public List<EventStreamResponse> getEventStreams() {
		return eventStreamService.retrieveAllEventStreams().stream().map(eventStream -> {
			List<ViewSpecification> views = List.of();
			ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(eventStream.getCollection());
			return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
					eventStream.getVersionOfPath(), views, shaclShape.getModel());
		}).toList();

	}

	@PutMapping(consumes = { JSON_LD, NQUADS, TURTLE })
	@Operation(summary = "Creates an Event Stream based on the provided config")
	public EventStreamResponse putEventStream(
			@Parameter(description = "A valid RDF model defining the Event Stream", example = "<s> <p> <o>")
			@RequestBody String configuredEventStream,
			@Parameter(hidden = true)
			@RequestHeader("Content-Type") String contentType) {
		Model eventStreamModel = ModelConverter.toModel(configuredEventStream, contentType);
		eventStreamValidator.validateShape(eventStreamModel);

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
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = NQUADS),
			@Content(mediaType = JSON_LD),
			@Content(mediaType = TURTLE)
	})
	public EventStreamResponse getEventStream(
			@Parameter(description = "Collection name") @PathVariable String collectionName) {
		EventStream eventStream = eventStreamService.retrieveEventStream(collectionName);
		List<ViewSpecification> views = List.of();
		ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(collectionName);

		return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), views, shaclShape.getModel());
	}

	@DeleteMapping("/{collectionName}")
	public ResponseEntity<Object> deleteEventStream(
			@Parameter(description = "Collection name") @PathVariable String collectionName) {
		// TODO: delete views by collectionName when this is added to the service
		eventStreamService.deleteEventStream(collectionName);
		shaclShapeService.deleteShaclShape(collectionName);
		return ResponseEntity.ok().build();
	}

}
